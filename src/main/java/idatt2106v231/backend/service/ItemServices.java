package idatt2106v231.backend.service;

import idatt2106v231.backend.dto.item.ItemDto;
import idatt2106v231.backend.model.Item;
import idatt2106v231.backend.model.ItemRefrigerator;
import idatt2106v231.backend.repository.CategoryRepository;
import idatt2106v231.backend.repository.ItemRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to manage Item objects.
 */
@Service
public class ItemServices {

    private final ItemRepository itemRepo;

    private final CategoryRepository categoryRepository;

    private final ModelMapper mapper;

    /**
     * Constructor which sets the Item repository to use for database access.
     */
    @Autowired
    public ItemServices(ItemRepository itemRepo, CategoryRepository categoryRepository) {
        this.itemRepo = itemRepo;
        this.categoryRepository = categoryRepository;
        this.mapper = new ModelMapper();
    }

    /**
     * Method to save a new item to database.
     *
     * @param item the new item
     * @return true if the item was saved
     */
    public boolean saveItem(ItemDto item) {
        try {
            Item it = Item.builder()
                    .name(item.getName())
                    .category(categoryRepository.findById(item.getCategoryId()).get())
                    .itemInRefrigerators(new ArrayList<>())
                    .itemInShoppingList(new ArrayList<>())
                    .build();
            itemRepo.save(it);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Method to delete an item from database.
     *
     * @param itemId the item id
     * @return true if the item was deleted
     */
    public boolean deleteItem(int itemId){
        try{
            itemRepo.deleteById(itemId);
            return true;
        } catch (Exception e){
            return false;
        }
    }

    /**
     * Method to get an item by name.
     *
     * @param name the items name
     * @return the item as a dto object
     */
    public ItemDto getItemByName(String name){
        try{
            return mapper.map(itemRepo.findByName(name).get(), ItemDto.class);
        }catch (Exception e){
            return null;
        }
    }

    /**
     * Method to get an item by id.
     *
     * @param itemId the items id
     * @return the item as a dto object
     */
    public ItemDto getItemById(int itemId){
        try{
            return mapper.map(itemRepo.findById(itemId).get(), ItemDto.class);
        }catch (Exception e){
            return null;
        }
    }

    /**
     * Method to get all items.
     *
     * @return list of Items as dto objects
     */
    public List<ItemDto> getAllItems(){
        try{
            return itemRepo
                    .findAll()
                    .stream()
                    .map(obj -> mapper.map(obj, ItemDto.class))
                    .toList();
        }catch (Exception e){
            return null;
        }
    }

    /**
     * Method to get all item by category.
     *
     * @param categoryId the category id
     * @return list of items as dto objects
     */
    public List<ItemDto> getAllItemsByCategory(int categoryId){
        try{
            return itemRepo
                    .findAllByCategoryCategoryId(categoryId)
                    .stream()
                    .map(obj -> mapper.map(obj, ItemDto.class))
                    .toList();
        }catch (Exception e){
            return null;
        }
    }

    /**
     * Method to check if an item not exists in database by name.
     *
     * @param name the items name
     * @return true if the item exists
     */
    public boolean itemNotExist(String name){
        return itemRepo.findByName(name).isEmpty();
    }

    /**
     * Method to check if an item not exists in database by id.
     *
     * @param itemId the items id
     * @return true if the item exists
     */
    public boolean itemNotExist(int itemId){
        return !itemRepo.existsById(itemId);
    }
}