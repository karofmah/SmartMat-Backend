package idatt2106v231.backend.service;


import idatt2106v231.backend.dto.refrigerator.ItemInRefrigeratorDto;
import idatt2106v231.backend.dto.refrigerator.RefrigeratorDto;
import idatt2106v231.backend.dto.item.ItemDto;
import idatt2106v231.backend.enums.Measurement;
import idatt2106v231.backend.model.ItemRefrigerator;
import idatt2106v231.backend.repository.ItemRefrigeratorRepository;
import idatt2106v231.backend.repository.ItemRepository;
import idatt2106v231.backend.repository.RefrigeratorRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to handle Refrigerator objects
 */
@Service
public class RefrigeratorServices {

    private RefrigeratorRepository refrigeratorRepository;
    private ItemRefrigeratorRepository itemRefrigeratorRepository;
    private ItemRepository itemRepository;

    private final ModelMapper mapper = new ModelMapper();

    @Autowired
    public void setRefrigeratorRepository(RefrigeratorRepository refrigeratorRepository) {
        this.refrigeratorRepository = refrigeratorRepository;
    }

    @Autowired
    public void setItemRefrigeratorRepository(ItemRefrigeratorRepository itemRefrigeratorRepository) {
        this.itemRefrigeratorRepository = itemRefrigeratorRepository;
    }
    @Autowired
    public void setItemRepository(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    /**
     * Method to get a refrigerator by id
     *
     * @param refrigeratorId the refrigerators id
     */
    public RefrigeratorDto getRefrigeratorById(int refrigeratorId) {
        try {
            return mapper.map(refrigeratorRepository.findById(refrigeratorId).get(), RefrigeratorDto.class);
        }
        catch (Exception e) {
            return null;
        }
    }

    /**
     * Method to get a refrigerator by user
     *
     * @param userEmail the user email
     */
    public RefrigeratorDto getRefrigeratorByUserEmail(String userEmail) {
        try {
            return mapper.map(refrigeratorRepository.findByUserEmail(userEmail).get(), RefrigeratorDto.class);
        }
        catch (Exception e) {
            return null;
        }
    }

    /**
     * Method to get all refrigerators
     *
     */
    public List<RefrigeratorDto> getAllRefrigerators() {
        try {
            List<RefrigeratorDto> list = new ArrayList<>();
            refrigeratorRepository.findAll().forEach(obj -> list.add(mapper.map(obj, RefrigeratorDto.class)));
            return list;
        }catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    /**
     * Method to get all items in refrigerator
     */
    public List<ItemDto> getItemsInRefrigerator(int refrigeratorId) {
        try {
            return refrigeratorRepository.findById(refrigeratorId).get()
                    .getItemsInRefrigerator().stream().map(obj -> mapper.map(obj.getItem(), ItemDto.class)).toList();
        }catch (Exception e) {
            return null;
        }
    }

    /**
     * Method to add item to refrigerator
     *
     * @return if the item is added
     */
    public boolean addItemToRefrigerator(ItemInRefrigeratorDto dto){
        try {
            var itemRef = ItemRefrigerator.builder()
                    .refrigerator(refrigeratorRepository.findById(dto.getRefrigeratorId()).get())
                    .item(itemRepository.findByName(dto.getItemName()).get())
                    .amount(dto.getAmount())
                    .measurementType(Measurement.L)
                    .build();
            itemRefrigeratorRepository.save(itemRef);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    /**
     * Method to delete item from refrigerator
     *
     * @return if the item is deleted
     */
    public boolean deleteItemFromRefrigerator(ItemInRefrigeratorDto dto){
        try {
          ItemRefrigerator item = itemRefrigeratorRepository
                  .findByItemNameAndRefrigeratorRefrigeratorId(dto.getItemName(), dto.getRefrigeratorId()).get();

            itemRefrigeratorRepository.delete(item);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    /**
     * Method to update amount of an item in refrigerator
     *
     * @return if the item is updated
     */
    public boolean updateItemInRefrigerator(ItemInRefrigeratorDto dto){
        try {
            ItemRefrigerator itemRefrigerator = itemRefrigeratorRepository
                    .findByItemNameAndRefrigeratorRefrigeratorId(dto.getItemName(), dto.getRefrigeratorId()).get();

            itemRefrigerator.updateAmount(dto.getAmount());
            itemRefrigeratorRepository.save(itemRefrigerator);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    /**
     * Checks if the refrigerator exists
     */
    public boolean refrigeratorExists(String email){
        return refrigeratorRepository.findByUserEmail(email).isPresent();
    }
    public boolean refrigeratorExists(int refrigeratorId){
        return refrigeratorRepository.findById(refrigeratorId).isPresent();
    }
}