package idatt2106v231.backend.service;

import idatt2106v231.backend.dto.refrigerator.EditItemInRefrigeratorDto;
import idatt2106v231.backend.dto.refrigerator.ItemInRefrigeratorDto;
import idatt2106v231.backend.dto.refrigerator.RefrigeratorDto;
import idatt2106v231.backend.enums.Measurement;
import idatt2106v231.backend.model.Garbage;
import idatt2106v231.backend.model.Item;
import idatt2106v231.backend.model.ItemExpirationDate;
import idatt2106v231.backend.model.ItemRefrigerator;
import idatt2106v231.backend.repository.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

/**
 * Class to manage Refrigerator objects.
 */
@Service
public class RefrigeratorServices {

    private RefrigeratorRepository refRepo;
    private ItemRepository itemRepo;
    private ItemRefrigeratorRepository itemRefRepo;
    private GarbageRepository garbRepo;
    private ItemExpirationDateRepository itemExpRepo;

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
     * Sets the itemRefrigerator repository to use for database access.
     *
     * @param itemRefRepo the itemRefrigerator repository to use
     */
    @Autowired
    public void setItemRefRepo(ItemRefrigeratorRepository itemRefRepo) {
        this.itemRefRepo = itemRefRepo;
    }

    @Autowired
    public void setItemExpRepo(ItemExpirationDateRepository itemExpRepo) {
        this.itemExpRepo = itemExpRepo;
    }

    /**
     * Sets the garbage repository to use for database access.
     *
     * @param garbRepo the garbage repository to use
     */

    @Autowired
    public void setGarbRepo(GarbageRepository garbRepo) {
        this.garbRepo = garbRepo;
    }

    /**
     * Method to get a refrigerator by user.
     *
     * @param userEmail the user email
     * @return the refrigerator as a dto object
     */
    public RefrigeratorDto getRefrigeratorByUserEmail(String userEmail) {
        try {
            int refrigeratorId = refRepo.findByUserEmail(userEmail)
                    .get()
                    .getRefrigeratorId();

            List<ItemInRefrigeratorDto> items = getItemsInRefrigerator(refrigeratorId);
            return new RefrigeratorDto(refrigeratorId, items);
        }
        catch (Exception e) {
            return null;
        }
    }

    /**
     * Method to get all items in a refrigerator.
     *
     * @param refrigeratorId the refrigerator id
     * @return the items in the refrigerator as dto objects
     */
    public List<ItemInRefrigeratorDto> getItemsInRefrigerator(int refrigeratorId) {
        try {
           return refRepo.findById(refrigeratorId)
                   .get()
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
     * Method to get all items in a refrigerator which match a specified category
     *
     * @param refrigeratorId the refrigerator id
     * @return the items in the refrigerator as dto objects
     */
    public List<ItemInRefrigeratorDto> getItemsInRefrigeratorByCategory(int refrigeratorId, int categoryId) {
        try {
            return refRepo.findById(refrigeratorId)
                    .get()
                    .getItemsInRefrigerator()
                    .stream()
                    .map(obj -> mapper.map(obj, ItemInRefrigeratorDto.class))
                    .filter(obj -> obj.getItem().getCategoryId() == categoryId)
                    .toList();
        }catch (Exception e) {
            return null;
        }
    }

    /**
     * Method to add an item to a refrigerator.
     *
     * @param itemRefDto the itemRefrigerator object to add
     * @return true if the item is added to the refrigerator
     */
    public boolean addItemToRefrigerator(EditItemInRefrigeratorDto itemRefDto){
        //try {
            var itemRef = ItemRefrigerator.builder()
                    .refrigerator(refRepo.findById(itemRefDto.getRefrigeratorId()).get())
                    .item(itemRepo.findByName(itemRefDto.getItemName()).get())
                    //.amount(itemRefDto.getAmount())
                    //.measurementType(Measurement.L)
                    .build();
            int newEntityId = itemRefRepo.save(itemRef).getItemRefrigeratorId();
        //System.out.println(item.getName());
            var itemExpirationDate = ItemExpirationDate.builder()
                    .measurement(itemRefDto.getMeasurementType())
                    .amount(itemRefDto.getAmount())
                    .date(itemRefDto.getDate())
                    //.itemRefrigerator(itemRefRepo.findByItemNameAndRefrigeratorRefrigeratorId(item.getName(), itemRefDto.getRefrigeratorId()).get())
                    .itemRefrigerator(itemRefRepo.findById(newEntityId).get())
                    .build();

        System.out.println(itemRefDto.getDate());
        //System.out.println(itemRefRepo.findById(itemRefDto.getItemRefrigeratorId()).get().getItem().getName());
            itemExpRepo.save(itemExpirationDate);
            return true;
        /*}catch (Exception e){
            System.out.println(e);
            return false;
        }*/
    }

    /**
     * Method to delete item from refrigerator.
     *
     * @param itemRefDto the item to be removed
     * @return true if the item is deleted
     */
    public boolean deleteItemFromRefrigerator(EditItemInRefrigeratorDto itemRefDto){
        try {
            ItemRefrigerator item = itemRefRepo
                    .findByItemNameAndRefrigeratorRefrigeratorId(itemRefDto.getItemName(), itemRefDto.getRefrigeratorId())
                    .get();

            ItemExpirationDate itemExpirationDate = itemExpRepo.findTopByItemRefrigerator_ItemRefrigeratorIdOrderByDate(itemRefDto.getRefrigeratorId()).get();
            //ItemExpirationDate itemExp = itemExpRepo.findDistinctByItemRefrigerator_ItemRefrigeratorId(itemRefDto.getItemExpirationDateId()).get();

            if (itemRefDto.getAmount() >= itemExpirationDate.getAmount()){
                itemRefRepo.delete(item);
            }else{
                itemExpirationDate.addAmount(-itemRefDto.getAmount());
                itemRefRepo.save(item);
            }
            return true;
        }catch (Exception e){
            return false;
        }
    }

    /**
     * Method to add waste in the garbage table in the database,
     * and delete the item from the refrigerator.
     *
     * @param itemRefDto the garbage
     * @return true if the item is deleted
     */
    public boolean addToGarbage(EditItemInRefrigeratorDto itemRefDto){
        try {
            Optional<Garbage> garbage = garbRepo.findByRefrigeratorRefrigeratorIdAndDate(itemRefDto.getRefrigeratorId(), YearMonth.now());
            Garbage gar;
            if (garbage.isPresent()){
                gar = garbage.get();
                gar.updateAmount(itemRefDto.getAmount());
            }
            else{
                gar = mapper.map(itemRefDto, Garbage.class);
                gar.setDate(YearMonth.now());
            }
            garbRepo.save(gar);
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
    public boolean updateItemInRefrigeratorAmount(EditItemInRefrigeratorDto itemRefDto){
        try {
            ItemRefrigerator itemRefrigerator = itemRefRepo
                    .findByItemNameAndRefrigeratorRefrigeratorId(itemRefDto.getItemName(), itemRefDto.getRefrigeratorId())
                    .get();

            List<ItemExpirationDate> allEqualItemsInRefrigerator = itemExpRepo.findAllByItemRefrigerator_ItemRefrigeratorId(itemRefrigerator.getItemRefrigeratorId());

            for(ItemExpirationDate itemExpDate : allEqualItemsInRefrigerator) {
                if(itemExpDate.getDate().equals(itemRefDto.getDate()) || itemExpDate.getDate() == null) {
                    itemExpDate.addAmount(itemRefDto.getAmount());
                    return true;
                }
            }

            return false;

            /*if(itemExpRepo.findByItemRefrigerator_ItemRefrigeratorId(itemRefrigerator.getItemRefrigeratorId()).isPresent()) {
                &&
            }*/
            //ItemExpirationDate itemExpirationDate = itemExpRepo.findTopByItemRefrigerator_ItemRefrigeratorIdOrderByDate(itemRefDto.getRefrigeratorId()).get();
            //ItemExpirationDate itemExpirationDate = itemExpRepo.findDistinctByItemRefrigerator_ItemRefrigeratorId(itemRefDto.getItemExpirationDateId()).get();
            //itemExpirationDate.updateAmount(itemRefDto.getAmount());
            //itemRefRepo.save(itemRefrigerator);
            //return true;
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