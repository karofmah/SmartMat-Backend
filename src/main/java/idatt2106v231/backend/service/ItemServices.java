package idatt2106v231.backend.service;

import idatt2106v231.backend.dto.item.ItemDto;
import idatt2106v231.backend.model.Item;


import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.stereotype.Service;

@Service
public class ItemServices {

    /*
    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CategoryRepository categoryRepository;
    */


    private final ModelMapper mapper = new ModelMapper();

    /**
     * Method to save a new item to database
     *
     * @param item the new item
     */
    public boolean saveItem(ItemDto item) {
        try {
            Item it = mapper.map(item, Item.class);
            it.setCategory(categoryRepository.findById(item.getCategory()).get());
            itemRepository.save(it);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Method to delete an item from database
     *
     * @param itemId the items id
     */
    public void deleteItem(int itemId){
      //  itemRepository.deleteById(itemId);
    }

    /**
     * Method to get an item
     *
     * @param itemId the items id
     */
    public void getItem(int itemId){
    }

    /**
     * Method to get all items
     *
     */
    public void getAllItems(){
    }

    /**
     * Method to get all item by category
     *
     * @param categoryId the category id
     */
    public void getAllItemsByCategory(int categoryId){
    }
    /**
     * Method to get the amount of refrigerators containing a specific item
     *
     * @param itemId the items id
     */
    public void getAmountOfRefrigeratorsContainingItem(int itemId){
    }

    public boolean checkIfItemExists(String name){
        return itemRepository.findByName(name).isPresent();
    }
    public boolean checkIfItemExists(int itemId){
        return itemRepository.existsById(itemId);
    }
}
