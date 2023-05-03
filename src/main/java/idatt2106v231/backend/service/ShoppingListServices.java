package idatt2106v231.backend.service;

import idatt2106v231.backend.dto.shoppinglist.ItemInShoppingListCreationDto;
import idatt2106v231.backend.dto.shoppinglist.ItemShoppingListDto;
import idatt2106v231.backend.dto.shoppinglist.ShoppingListDto;
import idatt2106v231.backend.enums.Measurement;
import idatt2106v231.backend.model.ItemShoppingList;
import idatt2106v231.backend.repository.ItemRepository;
import idatt2106v231.backend.repository.ItemShoppingListRepository;
import idatt2106v231.backend.repository.ShoppingListRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShoppingListServices {

    private final ItemRepository itemRepository;

    private final ShoppingListRepository shoppingListRepository;

    private final ItemShoppingListRepository itemShoppingListRepository;

    private final ModelMapper mapper;

    @Autowired
    public ShoppingListServices(ItemRepository itemRepository, ShoppingListRepository shoppingListRepository, ItemShoppingListRepository itemShoppingListRepository) {
        this.itemRepository = itemRepository;
        this.shoppingListRepository = shoppingListRepository;
        this.itemShoppingListRepository = itemShoppingListRepository;
        this.mapper = new ModelMapper();

        TypeMap<ItemShoppingList, ItemShoppingListDto> propertyMapper = mapper.createTypeMap(ItemShoppingList.class, ItemShoppingListDto.class);
        TypeMap<ItemShoppingListDto, ItemShoppingList> propertyMapper2 = mapper.createTypeMap(ItemShoppingListDto.class, ItemShoppingList.class);
    }



    public ShoppingListDto getShoppingListByUserEmail(String email) {
        try {
            int shoppingListId = shoppingListRepository.findDistinctByUserEmail(email).get().getShoppingListId();
            List<ItemShoppingListDto> items = getAllItemsFromShoppingList(shoppingListId);

            return new ShoppingListDto(shoppingListId, items);

        } catch(Exception e) {
            return null;
        }
    }
    public List<ItemShoppingListDto> getAllItemsFromShoppingList(int shoppingListId) {
        try {
            return shoppingListRepository
                    .findById(shoppingListId)
                    .get()
                    .getItemShoppingList()
                    .stream()
                    .map(obj -> mapper.map(obj, ItemShoppingListDto.class))
                    .toList();
        } catch(Exception e) {
            return null;
        }
    }

    public boolean saveItemToShoppingList(ItemInShoppingListCreationDto itemInShoppingListCreationDto) {
        try {
            var itemShoppingList = ItemShoppingList.builder()
                            .item(itemRepository.findByName(itemInShoppingListCreationDto.getItemName()).get())
                            .amount(itemInShoppingListCreationDto.getAmount())
                            .measurement(Measurement.L)
                            .shoppingList(shoppingListRepository.findById(itemInShoppingListCreationDto.getShoppingListId()).get())
                            .build();
            itemShoppingListRepository.save(itemShoppingList);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean deleteItemFromShoppingList(ItemInShoppingListCreationDto itemInShoppingListCreationDto) {
        try {
            ItemShoppingList item = itemShoppingListRepository
                    .findByItemNameAndShoppingList_ShoppingListId(itemInShoppingListCreationDto.getItemName(), itemInShoppingListCreationDto.getShoppingListId())
                    .get();
            itemShoppingListRepository.delete(item);
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    public boolean shoppingListExists(int shoppingListId) {
        return shoppingListRepository.findById(shoppingListId).isPresent();
    }

    public boolean itemExistsInShoppingList(int shoppingListId, String itemName) {
        return itemShoppingListRepository.findByItemNameAndShoppingList_ShoppingListId(itemName, shoppingListId).isPresent();
    }
}
