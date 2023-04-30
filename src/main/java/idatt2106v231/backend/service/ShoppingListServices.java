package idatt2106v231.backend.service;

import idatt2106v231.backend.dto.shoppinglist.ItemInShoppingListCreationDto;
import idatt2106v231.backend.dto.shoppinglist.ItemShoppingListDto;
import idatt2106v231.backend.dto.shoppinglist.ShoppingListDto;
import idatt2106v231.backend.model.ItemShoppingList;
import idatt2106v231.backend.repository.ItemRepository;
import idatt2106v231.backend.repository.ItemShoppingListRepository;
import idatt2106v231.backend.repository.ShoppingListRepository;
import idatt2106v231.backend.repository.SubUserRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShoppingListServices {

    private final ItemRepository itemRepository;

    //@Autowired
    private final ShoppingListRepository shoppingListRepository;

    //@Autowired
    private final ItemShoppingListRepository itemShoppingListRepository;

    private final SubUserRepository subUserRepository;

    private final ModelMapper mapper = new ModelMapper();

    /*@Autowired
    public void setItemRepository(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }*/

    @Autowired
    public ShoppingListServices(ItemRepository itemRepository, ShoppingListRepository shoppingListRepository, ItemShoppingListRepository itemShoppingListRepository, SubUserRepository subUserRepository) {
        this.itemRepository = itemRepository;
        this.shoppingListRepository = shoppingListRepository;
        this.itemShoppingListRepository = itemShoppingListRepository;
        this.subUserRepository = subUserRepository;

        TypeMap<ItemShoppingList, ItemShoppingListDto> propertyMapper = mapper.createTypeMap(ItemShoppingList.class, ItemShoppingListDto.class);
        propertyMapper.addMappings(mapper -> mapper.map(obj -> obj.getSubUser().isAccessLevel(), ItemShoppingListDto::setSubUserAccessLevel));
        TypeMap<ItemShoppingListDto, ItemShoppingList> propertyMapper2 = mapper.createTypeMap(ItemShoppingListDto.class, ItemShoppingList.class);
        //propertyMapper.addMappings(mapper -> mapper.map(obj -> obj.getShoppingList().getShoppingListId(), ItemShoppingList::setShoppingList));
        //propertyMapper2.addMappings(mapper -> mapper.map(obj -> this.itemRepository.findByName(obj.getItemName()).get(), ItemShoppingList::setItem));
        //propertyMapper2.addMappings(mapper -> mapper.map(obj -> this.shoppingListRepository.findById(obj.getShoppingListId()).get(), ItemShoppingList::setShoppingList));
        //propertyMapper2.addMappings(mapper -> mapper.map(obj -> Measurement.L, ItemShoppingList::setMeasurement));
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
        /*List<ItemShoppingList> items = itemShoppingListRepository.findAllByShoppingListShoppingListId(shoppingListRepository.findDistinctByUserEmail(email).get().getShoppingListId());
        List<ItemShoppingListDto> itemDtos = new ArrayList<>();

        items.forEach(obj -> itemDtos.add(mapper.map(obj, ItemShoppingListDto.class)));
        return itemDtos;*/
    }

    public boolean saveItemToShoppingList(ItemInShoppingListCreationDto itemInShoppingListCreationDto) {
        try {
            System.out.println(itemInShoppingListCreationDto.getMeasurementType());
            ItemShoppingList itemShoppingList = ItemShoppingList.builder()
                            .item(itemRepository.findByName(itemInShoppingListCreationDto.getItemName()).get())
                            .amount(itemInShoppingListCreationDto.getAmount())
                            .measurementType(itemInShoppingListCreationDto.getMeasurementType())
                            .shoppingList(shoppingListRepository.findById(itemInShoppingListCreationDto.getShoppingListId()).get())
                            .subUser(subUserRepository.findById(itemInShoppingListCreationDto.getShoppingListId()).get())
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

            /*var itemRef = ItemShoppingList.builder()
                    .item(itemRepository.findByName(itemShoppingListDto.getItemName()).get())
                    .amount(itemShoppingListDto.getAmount())
                    .measurement(Measurement.L)
                    .shoppingList(shoppingListRepository.findById(itemShoppingListDto.getShoppingListId()).get())
                    .build();
            itemShoppingListRepository.delete(itemRef);
            return true;*/
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
