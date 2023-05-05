package idatt2106v231.backend.service;

import idatt2106v231.backend.dto.item.ItemDto;
import idatt2106v231.backend.dto.refrigerator.EditItemInRefrigeratorDto;
import idatt2106v231.backend.dto.refrigerator.ItemInRefrigeratorDto;
import idatt2106v231.backend.dto.refrigerator.RefrigeratorDto;
import idatt2106v231.backend.dto.refrigerator.*;
import idatt2106v231.backend.model.ItemExpirationDate;
import idatt2106v231.backend.model.ItemRefrigerator;
import idatt2106v231.backend.repository.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Class to manage Refrigerator objects.
 */
@Service
public class RefrigeratorServices {

    private final RefrigeratorRepository refRepo;
    private final ItemRepository itemRepo;
    private final ItemRefrigeratorRepository itemRefRepo;
    private final ItemExpirationDateRepository itemExpRepo;

    private final MeasurementServices measurementServices;

    private final ModelMapper mapper;

    /**
     * Constructor which sets the repositories to use for database access and services.
     */
    @Autowired
    public RefrigeratorServices(RefrigeratorRepository refRepo, ItemRepository itemRepo,
                                ItemRefrigeratorRepository itemRefRepo, MeasurementServices measurementServices,
                                ItemExpirationDateRepository itemExpRepo) {
        this.refRepo = refRepo;
        this.itemRepo = itemRepo;
        this.itemRefRepo = itemRefRepo;
        this.measurementServices = measurementServices;
        this.itemExpRepo = itemExpRepo;
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
            int refrigeratorId = refRepo
                    .findByUserEmail(userEmail)
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
            List<ItemInRefrigeratorDto> list = refRepo
                    .findById(refrigeratorId)
                    .get()
                    .getItemsInRefrigerator()
                    .stream()
                    .map(obj -> mapper.map(obj, ItemInRefrigeratorDto.class))
                    .toList();

            list.forEach(obj -> obj.setItemsInRefrigerator(getItemExpirationInRefrigerator(obj.getItemRefrigeratorId())));

            return list;
        }catch (Exception e) {
            return null;
        }
    }

    /**
     * Method to get all items of a specific item in a refrigerator.
     *
     * @param itemRefrigeratorId the items in refrigerator id
     * @return the items in the refrigerator as dto objects
     */
    public List<ItemExpirationDateDto> getItemExpirationInRefrigerator(int itemRefrigeratorId) {
        try {
            List<ItemExpirationDateDto> list = itemRefRepo
                    .findById(itemRefrigeratorId)
                    .get()
                    .getItemExpirationDates()
                    .stream()
                    .map(obj -> mapper.map(obj, ItemExpirationDateDto.class))
                    .toList();

            return list;
        }catch (Exception e) {
            return null;
        }
    }

    /**
     * Method to get all items in a refrigerator who match a specified category
     *
     * @param refrigeratorId the refrigerator id
     * @return the items in the refrigerator as dto objects
     */
    public List<ItemInRefrigeratorDto> getItemsInRefrigeratorByCategory(int refrigeratorId, int categoryId) {
        try {
            List<ItemInRefrigeratorDto> list = refRepo
                    .findById(refrigeratorId)
                    .get()
                    .getItemsInRefrigerator()
                    .stream()
                    .map(obj -> mapper.map(obj, ItemInRefrigeratorDto.class))
                    .filter(obj -> obj.getItem().getCategoryId() == categoryId)
                    .toList();

            list.forEach(obj -> obj.setItemsInRefrigerator(getItemExpirationInRefrigerator(obj.getItemRefrigeratorId())));

            return list;
        }catch (Exception e) {
            return null;
        }
    }

    /**
     * Method to get all items in a refrigerator by expiration date
     *
     * @param refrigeratorId the refrigerator id
     * @return the items in the refrigerator as dto objects
     */
    public List<ItemInRefrigeratorDto> getItemsInRefrigeratorByExpirationDate(Date start, Date end, int refrigeratorId) {
        try {
            List<ItemExpirationDateDto> itemWhichExpires = itemExpRepo
                    .findAllByItemRefrigerator_RefrigeratorRefrigeratorIdAndDateGreaterThanAndDateLessThanEqual(
                            refrigeratorId,
                            start,
                            end)
                    .stream()
                    .map(obj -> mapper.map(obj, ItemExpirationDateDto.class))
                    .toList();

            List<ItemInRefrigeratorDto> list = getItemsInRefrigerator(refrigeratorId);

            list.forEach(obj ->
                obj.setItemsInRefrigerator(
                        obj.getItemsInRefrigerator()
                                .stream()
                                .filter(itemWhichExpires::contains)
                                .toList())
            );

            return list.stream()
                    .filter(obj -> !obj.getItemsInRefrigerator().isEmpty())
                    .toList();
        } catch(Exception e) {
            return null;
        }
    }

    /**
     * Method to get specified amount of items in a refrigerator by expiration date
     *
     * @param refrigeratorId the refrigerator id
     * @return the items in the refrigerator as dto objects
     */
    public List<ItemInRefrigeratorDto> getTopItemsInRefrigeratorByExpirationDate(int refrigeratorId, int amount) {
        try {
            List<ItemExpirationDateDto> itemWhichExpires = itemExpRepo
                    .findAllByItemRefrigerator_RefrigeratorRefrigeratorIdOrderByDate(refrigeratorId)
                    .stream()
                    .limit(amount)
                    .map(obj -> mapper.map(obj, ItemExpirationDateDto.class))
                    .toList();

            List<ItemInRefrigeratorDto> list = getItemsInRefrigerator(refrigeratorId);

            list.forEach(obj -> obj.setItemsInRefrigerator(
                    obj.getItemsInRefrigerator()
                            .stream()
                            .filter(itemWhichExpires::contains)
                            .toList())
            );

            return list.stream()
                    .filter(obj -> !obj.getItemsInRefrigerator().isEmpty())
                    .toList();
        } catch(Exception e) {
            return null;
        }
    }

    /**
     * Method to add a new item to a refrigerator. The method first creates a row in ItemRefrigerator,
     * then a row in ItemExpirationDate.
     *
     * @param itemRefDto the item object to add to the database
     * @return true if the item is added to the refrigerator, false if something crashed in the process
     */
    public boolean addItemInRefrigerator(ItemInRefrigeratorCreationDto itemRefDto){
        try {
            ItemRefrigerator itemRef = ItemRefrigerator.builder()
                        .refrigerator(refRepo.findById(itemRefDto.getRefrigeratorId()).get())
                        .item(itemRepo.findByName(itemRefDto.getItemName()).get())
                        .measurementType(itemRefDto.getMeasurementType())
                        .build();
            itemRefRepo.save(itemRef);

            var itemExpirationDate = ItemExpirationDate.builder()
                    .amount(itemRefDto.getAmount())
                    .date(parseDate(itemRefDto.getDate()))
                    .itemRefrigerator(itemRef)
                    .build();

            itemExpRepo.save(itemExpirationDate);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    /**
     * Method to add an item to a refrigerator which already contains said item.
     * Creates a new row in ItemExpirationDate table, but uses the pre-existing
     * id in ItemRefrigerator
     *
     * @param itemRefDto the item object to add to the database
     * @return true if the item is added to the refrigerator, false if something crashed in the process
     */
    public boolean addExpirationDateItem(ItemInRefrigeratorCreationDto itemRefDto){
        try {
            ItemRefrigerator itemRef = itemRefRepo
                    .findByItemNameAndRefrigeratorRefrigeratorId(
                            itemRefDto.getItemName(),
                            itemRefDto.getRefrigeratorId()
                    ).get();


            double amount = measurementServices
                    .changeAmountToWantedMeasurement(
                            itemRefDto.getAmount(),
                            itemRefDto.getMeasurementType(),
                            itemRef.getMeasurementType(),
                            itemRef.getItem().getName()
                    );

            var itemExpirationDate = ItemExpirationDate.builder()
                    .amount(amount)
                    .date(parseDate(itemRefDto.getDate()))
                    .itemRefrigerator(itemRef)
                    .build();

            itemExpRepo.save(itemExpirationDate);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    /**
     * Method to delete an amount of an item from a refrigerator. Remove or updates the item
     * based on how much is removed and how much is left
     *
     * @param itemRefDto the item and amount to be removed
     * @return true if the item is deleted, false if something crashed in the process
     */
    public boolean deleteItemFromRefrigerator(ItemInRefrigeratorRemovalDto itemRefDto){
        try {
            ItemExpirationDate itemExp = itemExpRepo
                    .findById(itemRefDto.getItemExpirationDateId())
                    .get();

            ItemRefrigerator itemRef = itemExp.getItemRefrigerator();

            if (itemRefDto.getAmount() >= itemExp.getAmount()){
                itemRef.getItemExpirationDates().remove(itemExp);
                itemExpRepo.delete(itemExp);
                if (itemRef.getItemExpirationDates().isEmpty()){
                    itemRefRepo.delete(itemRef);
                }
            }
            else {
                itemExp.updateAmount(-itemRefDto.getAmount());
                itemExpRepo.save(itemExp);
            }

            return true;
        }catch (Exception e){
            return false;
        }
    }

    /**
     * Method to add an amount of a pre-existing item to a refrigerator.
     *
     * @param itemRefDto the itemRefrigerator object with updated information
     * @return true if the item is updated
     */
    public boolean updateItemInRefrigerator(EditItemInRefrigeratorDto itemRefDto){
        try {
            ItemExpirationDate itemExp = itemExpRepo
                    .findById(itemRefDto.getItemExpirationDateId())
                    .get();

            ItemRefrigerator itemRef = itemExp.getItemRefrigerator();

            double amount = measurementServices
                    .changeAmountToWantedMeasurement(
                            itemRefDto.getAmount(),
                            itemRefDto.getMeasurementType(),
                            itemRef.getMeasurementType(),
                            itemRef.getItem().getName()
                            );

            itemExp.setAmount(amount);
            itemExp.setDate(itemRefDto.getDate());
            itemExpRepo.save(itemExp);

            return true;
        }catch (Exception e){
            return false;
        }
    }

    /**
     * Checks if the refrigerator not exist in database by id.
     *
     * @param refrigeratorId the refrigerators id
     * @return true if the refrigerator exists
     */
    public boolean refrigeratorNotExists(int refrigeratorId){
        return !refRepo.existsByRefrigeratorId(refrigeratorId);
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

    /**
     * Method to check if an item exists in the ItemExpirationDate table
     *
     * @param itemExpirationDateId id of the item to check
     * @return true or false based on its existence
     */
    public boolean itemExpirationDateNotExists(int itemExpirationDateId) {
        return !itemExpRepo.existsByItemExpirationDateId(itemExpirationDateId);
    }

    /**
     * Gets the n most popular items across all refrigerators
     *
     * @param n the number of items
     * @return the items
     */
    public List<ItemDto> getNMostPopularItems(int n) {
        return itemRefRepo
                .findAll()
                .stream()
                .map(ItemRefrigerator::getItem)
                .collect(Collectors.groupingBy(i -> i, Collectors.counting()))
                .entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .map(Map.Entry::getKey)
                .limit(n)
                .map(obj -> mapper.map(obj, ItemDto.class))
                .toList();
    }

    private Date parseDate(String date){
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(date);
        }catch (Exception e){
            return null;
        }
    }
}