package idatt2106v231.backend.service;

import idatt2106v231.backend.dto.item.ItemDto;
import idatt2106v231.backend.model.Item;


import idatt2106v231.backend.repository.CategoryRepository;
import idatt2106v231.backend.repository.ItemRefrigeratorRepository;
import idatt2106v231.backend.repository.ItemRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ItemServices {


    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private ItemRefrigeratorRepository itemRefrigeratorRepository;
    @Autowired
    private CategoryRepository categoryRepository;



    private final ModelMapper mapper = new ModelMapper();

    /**
     * Method to save a new item to database
     *
     * @param item the new item
     * @return if the item was saved
     */
    public boolean saveItem(ItemDto item) {
        try {
            Item it = mapper.map(item, Item.class);
            it.setCategory(categoryRepository.findById(item.getCategoryId()).get());
            itemRepository.save(it);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Method to delete an item from database
     *
     * @param itemId the item id
     * @return if the item was deleted
     */
    public boolean deleteItem(int itemId){
        try{
            itemRepository.deleteById(itemId);
            return true;
        }
        catch (Exception e){
            return false;
        }
    }

    /**
     * Method to get an item by name
     *
     * @param name the items name
     * @return the item
     */
    public ItemDto getItemByName(String name){
        try{
            return mapper.map(itemRepository.findByName(name).get(), ItemDto.class);
        }catch (Exception e){
            return null;
        }
    }

    /**
     * Method to get an item by id
     *
     * @param itemId the items id
     * @return the item
     */
    public ItemDto getItemById(int itemId){
        try{
            return mapper.map(itemRepository.findById(itemId).get(), ItemDto.class);
        }catch (Exception e){
            return null;
        }
    }

    /**
     * Method to get all items
     *
     * @return list of ItemDto objects
     */
    public List<ItemDto> getAllItems(){
        try{
            List<ItemDto> list = new ArrayList<>();
            itemRepository.findAll().forEach(obj -> list.add(mapper.map(obj, ItemDto.class)));
            return list;
        }catch (Exception e){
            return null;
        }
    }

    /**
     * Method to get all item by category
     *
     * @param categoryId the category id
     * @return list of ItemDto objects
     */
    public List<ItemDto> getAllItemsByCategory(int categoryId){
        try{
            List<ItemDto> list = new ArrayList<>();
            categoryRepository.findById(categoryId).get().getItems().forEach(obj -> list.add(mapper.map(obj, ItemDto.class)));
            return list;
        }catch (Exception e){
            return null;
        }
    }

    /**
     * Method to get the amount of refrigerators containing a specific item
     *
     * @param itemId the items id
     * @return the amount
     */
    public int getAmountOfRefrigeratorsContainingItem(int itemId){
        if (checkIfItemExists(itemId)){
            return itemRepository.findById(itemId).get().getItemInRefrigerators().size();
        }
        return -1;
    }

    /**
     * Method to check if item exists by name
     *
     * @param name the items name
     * @return if the item exists
     */
    public boolean checkIfItemExists(String name){
        return itemRepository.findByName(name).isPresent();
    }

    /**
     * Method to check if item exists by id
     *
     * @param itemId the items id
     * @return if the item exists
     */
    public boolean checkIfItemExists(int itemId){
        return itemRepository.existsById(itemId);
    }

    public boolean itemExistInRefrigerator(String itemName, int refrigeratorId){
        return itemRefrigeratorRepository.findByItemNameAndRefrigeratorRefrigeratorId(itemName, refrigeratorId).isPresent();
    }
}