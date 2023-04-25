package idatt2106v231.backend.service;


import idatt2106v231.backend.dto.refrigerator.ItemInRefrigeratorCreationDto;
import idatt2106v231.backend.dto.refrigerator.ItemInRefrigeratorDto;
import idatt2106v231.backend.dto.refrigerator.RefrigeratorDto;
import idatt2106v231.backend.enums.Measurement;
import idatt2106v231.backend.model.Item;
import idatt2106v231.backend.model.ItemRefrigerator;
import idatt2106v231.backend.model.Refrigerator;
import idatt2106v231.backend.model.User;
import idatt2106v231.backend.repository.ItemRefrigeratorRepository;
import idatt2106v231.backend.repository.ItemRepository;
import idatt2106v231.backend.repository.RefrigeratorRepository;
import idatt2106v231.backend.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Class to handle Refrigerator objects.
 */
@Service
public class RefrigeratorServices {

    private RefrigeratorRepository refRepo;
    private UserRepository userRepository;
    private ItemRefrigeratorRepository itemRefRepo;
    private ItemRepository itemRepo;

    private final ModelMapper mapper = new ModelMapper();

    /**
     * Sets the refrigerator repository to use for database access.
     *
     * @param refRepo the refrigerator repository to use
     */
    @Autowired
    public void setRefRepo(RefrigeratorRepository refRepo) {
        this.refRepo = refRepo;
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Sets the itemRefrigerator repository to use for database access.
     *
     * @param itemRefRepo the itemRefrigerator repository to use
     */
    @Autowired
    public void setItemRefRepo(ItemRefrigeratorRepository itemRefRepo) {
        this.itemRefRepo = itemRefRepo;
    }

    /**
     * Sets the item repository to use for database access.
     *
     * @param itemRepo the item repository to use
     */
    @Autowired
    public void setItemRepo(ItemRepository itemRepo) {
        this.itemRepo = itemRepo;
    }

    /**
     * Method to get a refrigerator by user.
     *
     * @param userEmail the user email
     * @return the refrigerator as a dto object
     */
    public RefrigeratorDto getRefrigeratorByUserEmail(String userEmail) {
        try {
            List<ItemInRefrigeratorDto> items = getItemsInRefrigerator(userEmail);
            int refrigeratorId = refRepo.findByUserEmail(userEmail).get().getRefrigeratorId();

            return new RefrigeratorDto(refrigeratorId, items);
        }
        catch (Exception e) {
            return null;
        }
    }

    /**
     * Method to get all items in a refrigerator.
     *
     * @param userEmail the user of the refrigerator
     * @return the items in the refrigerator as dto objects
     */
    public List<ItemInRefrigeratorDto> getItemsInRefrigerator(String userEmail) {
        try {
           return userRepository
                   .findByEmail(userEmail)
                   .get()
                   .getRefrigerator()
                   .getItemsInRefrigerator()
                   .stream()
                   .map(obj -> mapper.map(obj, ItemInRefrigeratorDto.class))
                   .toList();
        }catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    /**
     * Method to add an item to a refrigerator.
     *
     * @param itemRefDto the itemRefrigerator object to add
     * @return true if the item is added to the refrigerator
     */
    public boolean addItemToRefrigerator(ItemInRefrigeratorCreationDto itemRefDto){
        try {
            var itemRef = ItemRefrigerator.builder()
                    .refrigerator(refRepo.findById(itemRefDto.getRefrigeratorId()).get())
                    .item(itemRepo.findByName(itemRefDto.getItemName()).get())
                    .amount(itemRefDto.getAmount())
                    .measurementType(Measurement.L)
                    .build();
            itemRefRepo.save(itemRef);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    /**
     * Method to delete item from refrigerator.
     *
     * @param itemName the item to be removed
     * @param refrigeratorId the refrigerator id
     * @return true if the item is deleted
     */
    public boolean deleteItemFromRefrigerator(String itemName, int refrigeratorId){
        try {
          ItemRefrigerator item = itemRefRepo
                  .findByItemNameAndRefrigeratorRefrigeratorId(itemName, refrigeratorId)
                  .get();
            itemRefRepo.delete(item);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    /**
     * Method to update amount of an item in a refrigerator.
     *
     * @param itemRefDto the itemRefrigerator object with updated information
     * @return true if the item is updated
     */
    public boolean updateItemInRefrigeratorAmount(ItemInRefrigeratorCreationDto itemRefDto){
        try {
            ItemRefrigerator itemRefrigerator = itemRefRepo
                    .findByItemNameAndRefrigeratorRefrigeratorId(itemRefDto.getItemName(), itemRefDto.getRefrigeratorId())
                    .get();

            itemRefrigerator.updateAmount(itemRefDto.getAmount());
            itemRefRepo.save(itemRefrigerator);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    /**
     * Checks if the refrigerator exists by id.
     *
     * @param refrigeratorId the refrigerators id
     * @return true if the refrigerator exists
     */
    public boolean refrigeratorExists(int refrigeratorId){
        return refRepo.findById(refrigeratorId).isPresent();
    }

    /**
     * Method to check if an item exists in a refrigerator.
     *
     * @param itemName the items name
     * @param refrigeratorId the refrigerators id
     * @return true if the item exists
     */
    public boolean refrigeratorContainsItem(String itemName, int refrigeratorId){
        return itemRefRepo.findByItemNameAndRefrigeratorRefrigeratorId(itemName, refrigeratorId).isPresent();
    }
}