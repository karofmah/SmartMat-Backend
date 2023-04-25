package idatt2106v231.backend.service;

import idatt2106v231.backend.dto.item.ItemDto;
import idatt2106v231.backend.dto.shoppinglist.ItemShoppingListDto;
import idatt2106v231.backend.dto.subuser.SubUserDto;
import idatt2106v231.backend.model.Item;
import idatt2106v231.backend.model.ItemShoppingList;
import idatt2106v231.backend.model.SubUser;
import idatt2106v231.backend.repository.ItemShoppingListRepository;
import idatt2106v231.backend.repository.ShoppingListRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ShoppingListServices {

    @Autowired
    private ShoppingListRepository shoppingListRepository;

    @Autowired
    private ItemShoppingListRepository itemShoppingListRepository;

    private final ModelMapper mapper = new ModelMapper();

    public ShoppingListServices() {
        TypeMap<ItemShoppingList, ItemShoppingListDto> propertyMapper = mapper.createTypeMap(ItemShoppingList.class, ItemShoppingListDto.class);
        propertyMapper.addMappings(mapper -> mapper.map(obj -> obj.getShoppingList().getShoppingListId(), ItemShoppingList::setShoppingList));

    }

    public List<ItemShoppingListDto> getAllItemsFromShoppingList(String email) {
        List<ItemShoppingList> items = itemShoppingListRepository.findAllByShoppingListShoppingListId(shoppingListRepository.findDistinctByUserEmail(email).get().getShoppingListId());
        List<ItemShoppingListDto> itemDtos = new ArrayList<>();

        items.forEach(obj -> itemDtos.add(mapper.map(obj, ItemShoppingListDto.class)));
        return itemDtos;
    }

    public boolean saveItemToShoppingList(ItemShoppingListDto itemShoppingListDto) {
        try {
            itemShoppingListRepository.save(mapper.map(itemShoppingListDto, ItemShoppingList.class));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
