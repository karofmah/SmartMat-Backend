package idatt2106v231.backend.service;

import idatt2106v231.backend.dto.refrigerator.EditItemInRefrigeratorDto;
import idatt2106v231.backend.dto.refrigerator.ItemInRefrigeratorDto;
import idatt2106v231.backend.dto.refrigerator.RefrigeratorDto;
import idatt2106v231.backend.enums.Measurement;
import idatt2106v231.backend.model.Garbage;
import idatt2106v231.backend.model.Item;
import idatt2106v231.backend.model.ItemExpirationDate;
import idatt2106v231.backend.model.Item;
import idatt2106v231.backend.model.ItemRefrigerator;
import idatt2106v231.backend.repository.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Class to manage Refrigerator objects.
 */
@Service
public class RefrigeratorServices {

    private final RefrigeratorRepository refRepo;
    private final ItemRepository itemRepo;
    private final ItemRefrigeratorRepository itemRefRepo;
    private ItemExpirationDateRepository itemExpRepo;

    private final MeasurementServices measurementServices;

    private final ModelMapper mapper;

    @Autowired
    public RefrigeratorServices(RefrigeratorRepository refRepo, ItemRepository itemRepo,
                                ItemRefrigeratorRepository itemRefRepo, MeasurementServices measurementServices) {
        this.refRepo = refRepo;
        this.itemRepo = itemRepo;
        this.itemRefRepo = itemRefRepo;
        this.measurementServices = measurementServices;
        this.mapper = new ModelMapper();
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
        try {
            var itemRef = ItemRefrigerator.builder()
                    .refrigerator(refRepo.findById(itemRefDto.getRefrigeratorId()).get())
                    .item(itemRepo.findByName(itemRefDto.getItemName()).get())
                    .build();

            int newEntityId = itemRefRepo.save(itemRef).getItemRefrigeratorId();

            var itemExpirationDate = ItemExpirationDate.builder()
                    .measurement(itemRefDto.getMeasurementType())
                    .amount(itemRefDto.getAmount())
                    .date(itemRefDto.getDate())
                    //.itemRefrigerator(itemRefRepo.findByItemNameAndRefrigeratorRefrigeratorId(item.getName(), itemRefDto.getRefrigeratorId()).get())
                    .itemRefrigerator(itemRefRepo.findById(newEntityId).get())
                    .build();

            itemExpRepo.save(itemExpirationDate);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    /**
     * Method to delete item from refrigerator.
     *
     * @param itemRefDto the item to be removed
     * @return true if the item is deleted
     */
    public boolean deleteItemFromRefrigerator(EditItemInRefrigeratorDto itemRefDto){
        try {
            ItemRefrigerator itemRefrigerator = itemRefRepo
                    .findByItemNameAndRefrigeratorRefrigeratorId(itemRefDto.getItemName(), itemRefDto.getRefrigeratorId())
                    .get();

            double amount = measurementServices.changeAmountToWantedMeasurement(itemRefDto, itemRefrigerator.getMeasurementType());

            ItemExpirationDate itemExpirationDate = itemExpRepo.findTopByItemRefrigerator_ItemRefrigeratorIdOrderByDate(itemRefDto.getRefrigeratorId()).get();


            if (amount >= itemExpirationDate.getAmount()){
                itemRefRepo.delete(itemRefrigerator);
            }else{
                itemExpirationDate.addAmount(-amount);
                itemExpRepo.save(itemExpirationDate);
            }
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

            double amount = measurementServices.changeAmountToWantedMeasurement(itemRefDto, itemRefrigerator.getMeasurementType());

            List<ItemExpirationDate> allEqualItemsInRefrigerator = itemExpRepo.findAllByItemRefrigerator_ItemRefrigeratorId(itemRefrigerator.getItemRefrigeratorId());

            for(ItemExpirationDate itemExpDate : allEqualItemsInRefrigerator) {
                if(itemExpDate.getDate().equals(itemRefDto.getDate()) || itemExpDate.getDate() == null) {
                    itemExpDate.addAmount(amount);
                    itemExpRepo.save(itemExpDate);
                    return true;
                }
            }

           // itemRefRepo.save(itemRefrigerator);
            return false;
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