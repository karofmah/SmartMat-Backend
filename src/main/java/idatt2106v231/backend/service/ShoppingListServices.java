package idatt2106v231.backend.service;

import idatt2106v231.backend.dto.item.ItemDto;
import idatt2106v231.backend.dto.shoppinglist.ItemInShoppingListCreationDto;
import idatt2106v231.backend.dto.shoppinglist.ItemShoppingListDto;
import idatt2106v231.backend.dto.shoppinglist.ShoppingListDto;
import idatt2106v231.backend.dto.shoppinglist.WeeklyMenuShoppingListDto;
import idatt2106v231.backend.enums.Measurement;
import idatt2106v231.backend.model.Category;
import idatt2106v231.backend.model.ItemShoppingList;
import idatt2106v231.backend.repository.*;
import idatt2106v231.backend.repository.ItemRepository;
import idatt2106v231.backend.repository.ItemShoppingListRepository;
import idatt2106v231.backend.repository.ShoppingListRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ShoppingListServices {

    private final ItemRepository itemRepo;
    private final ShoppingListRepository shpListRepo;
    private final ItemShoppingListRepository itemShpListRepo;
    private final SubUserRepository subUserRepo;
    private final CategoryRepository catRepo;

    private final AiServices aiServices;
    private final ItemServices itemServices;
    private final SubUserServices subUserServices;
    private final RefrigeratorServices refServices;

    private final ModelMapper mapper;

    /**
     * Constructor which sets the repositories to use for database access, and services.
     */
    @Autowired
    public ShoppingListServices(ItemRepository itemRepo, ShoppingListRepository shpListRepo,
                                ItemShoppingListRepository itemShpListRepo, AiServices aiServices,
                                CategoryRepository catRepo, ItemServices itemServices,
                                SubUserRepository subUserRepo, SubUserServices subUserServices,
                                RefrigeratorServices refServices) {
        this.itemRepo = itemRepo;
        this.shpListRepo = shpListRepo;
        this.itemShpListRepo = itemShpListRepo;
        this.subUserRepo = subUserRepo;
        this.aiServices = aiServices;
        this.catRepo = catRepo;
        this.itemServices = itemServices;
        this.subUserServices = subUserServices;
        this.refServices = refServices;
        this.mapper = new ModelMapper();

        TypeMap<ItemShoppingList, ItemShoppingListDto> propertyMapper = mapper.createTypeMap(ItemShoppingList.class, ItemShoppingListDto.class);
        propertyMapper.addMappings(mapper -> mapper.map(obj -> obj.getSubUser().isAccessLevel(), ItemShoppingListDto::setSubUserAccessLevel));
     }

    /**
     * Method to get a shoppinglist by user.
     *
     * @param userEmail the user userEmail
     * @return the shoppinglist as a dto object
     */
    public ShoppingListDto getShoppingListByUserEmail(String userEmail) {
        try {
            int shoppingListId = shpListRepo
                    .findDistinctByUserEmail(userEmail)
                    .get()
                    .getShoppingListId();

            List<ItemShoppingListDto> items = getAllItemsInShoppingList(shoppingListId);

            return new ShoppingListDto(shoppingListId, items);
        } catch(Exception e) {
            return null;
        }
    }

    /**
     * Method to get all items in a shoppinglist.
     *
     * @param shoppingListId the shoppinglist id
     * @return the items in the shoppingList as dto objects
     */
    public List<ItemShoppingListDto> getAllItemsInShoppingList(int shoppingListId) {
        try {
            return shpListRepo
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

    /**
     * Method to add a new item in a shoppinglist.
     *
     * @param dto the item object to add to the database
     * @return true if the item is added to the refrigerator, false if something crashed in the process
     */
    public boolean addItemToShoppingList(ItemInShoppingListCreationDto dto) {
        try {
            var itemShoppingList = ItemShoppingList.builder()
                            .item(itemRepo.findByName(dto.getItemName()).get())
                            .amount(dto.getAmount())
                            .measurementType(dto.getMeasurementType())
                            .shoppingList(shpListRepo.findById(dto.getShoppingListId()).get())
                            .subUser(subUserRepo.findById(dto.getSubUserId()).get())
                            .build();
            itemShpListRepo.save(itemShoppingList);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Method to delete an item from a shoppinglist.
     *
     * @param dto the item to delete
     * @return true if the item is deleted
     */
    public boolean deleteItemFromShoppingList(ItemInShoppingListCreationDto dto) {
        try {
            ItemShoppingList item = itemShpListRepo
                    .findById(dto.getItemShoppingListId())
                    .get();

            if(dto.getAmount() >= item.getAmount()) {
                itemShpListRepo.delete(item);
            } else {
                dto.setAmount(-dto.getAmount());
                updateAmount(dto);
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Adds a weekly menu recipe list to the shopping list of the user
     *
     * @param dto the dto containing user and ingredient information
     * @return if the weekly menu recipe list was added
     */ //TODO se på
    public boolean addWeeklyMenuToShoppingList(WeeklyMenuShoppingListDto dto) {

        Optional<Category> otherCategory = catRepo.findByDescription("Other");
        int otherCategoryId = otherCategory.map(Category::getCategoryId).orElse(1);

        String list = translateRecipeListToCorrectFormat(dto.getIngredients());

        String[] lines = list.split("\n");
        for (String line : lines) {
            String[] parts = line.split(";");
            String name = "";
            String category = "";
            String quantity = "";

            if (parts.length > 0) category = parts[0];
            if (parts.length > 1) name = parts[1];
            if (parts.length > 2) quantity = parts[2];

            String[] quantityAndMeasurement = quantity.split("(?<=\\d)(?=\\D)");

            quantity = quantityAndMeasurement[0];
            if (!quantity.matches("\\d+")) quantity = "1";
            Measurement measurement = Measurement.UNIT;

            if (quantityAndMeasurement.length > 1) {
                for(Measurement c : Measurement.values()) {
                    if (quantityAndMeasurement[1].equalsIgnoreCase(c.name())) measurement = c;
                }
            }

            if (itemServices.itemNotExist(name)) {
                Optional<Category> categoryOptional = catRepo.findByDescription(category);
                int categoryId;

                categoryId = categoryOptional.map(Category::getCategoryId).orElse(otherCategoryId);

                ItemDto itemDto = ItemDto.builder()
                        .categoryId(categoryId)
                        .name(name)
                        .build();

                itemServices.saveItem(itemDto);
            }


            ItemInShoppingListCreationDto itemInShoppingListCreationDto = ItemInShoppingListCreationDto
                    .builder()
                    .itemName(name)
                    .shoppingListId(dto.getShoppingListId())
                    .subUserId(dto.getSubUserId())
                    .amount(Integer.parseInt(quantity))
                    .measurementType(measurement)
                    .build();

            if (addItemToShoppingList(itemInShoppingListCreationDto)) {
            }
            else {
                return false;
            }
        }
        return true;
    }

    /**
     * Translates a recipe list to the correct format:
     * "Category, Item, Amount"
     *
     * @return the translated recipe list
     */
    private String translateRecipeListToCorrectFormat(List<String> recipeList) {

        List<Category> categories = catRepo.findAll();


        String query = "Write this shopping list with csv format, using semicolons " +
                "with the attributes category,ingredient,quantity." +
                " If no quantity is specified, use '1'." +
                " Use the categories ";


        for (Category category : categories) {
            query += category.getDescription() + ", ";
        }


        query += " and 'other'. Example: 'Meat;Beef;500g'. Here is the list: ";

        query += recipeList.toString();

        return aiServices.getChatCompletion(query);

    }

    /**
     * Gets user email by shopping list ID
     *
     * @param shoppingListId shopping list ID
     * @return the user email
     */
    public String getShoppingListUserEmail(int shoppingListId) {
        return shpListRepo
                .findById(shoppingListId)
                .get()
                .getUser()
                .getEmail();
    }

    /**
     * Gets a specific item in a shopping list
     *
     * @param itemShoppingListId the ID of the item in the shopping list
     * @return the item
     */ //TODO trengs denne, skal og være dto?
    public ItemShoppingList getItemInShoppingList(int itemShoppingListId) {
        Optional<ItemShoppingList> itemShoppingList = itemShpListRepo.findById(itemShoppingListId);
        return itemShoppingList.orElse(null);
    }

    /**
     * Gets a specific item from a shopping list
     *
     * @param shoppingListId the id of the shopping list
     * @param itemName the name of the item
     * @param subUserAccessLevel the access level of the subUser that added the item
     * @return the item
     */
    public ItemShoppingList getItemInShoppingList(int shoppingListId, String itemName, boolean subUserAccessLevel) {
        Optional<ItemShoppingList> item = itemShpListRepo
                .findByItemNameAndShoppingList_ShoppingListIdAndSubUserAccessLevel(
                        itemName,
                        shoppingListId,
                        subUserAccessLevel
                );
        return item.orElse(null);
    }

    /**
     * Updates the amount of an already existing item
     *
     * @param dto the ItemInShoppingListCreationDto
     * @return if the amount is updated
     */
    public boolean updateAmount(ItemInShoppingListCreationDto dto) {
        try {
            ItemShoppingList currentItem = getItemInShoppingList(
                    dto.getShoppingListId(),
                    dto.getItemName(),
                    subUserServices.getAccessLevel(dto.getSubUserId())
            );

            var itemShoppingList = ItemShoppingList.builder()
                    .itemShoppingListId(dto.getItemShoppingListId())
                    .itemShoppingListId(currentItem.getItemShoppingListId())
                    .item(itemRepo.findByName(dto.getItemName()).get())
                    .amount(dto.getAmount())
                    .measurementType(dto.getMeasurementType())
                    .shoppingList(shpListRepo.findById(dto.getShoppingListId()).get())
                    .subUser(subUserRepo.findById(dto.getSubUserId()).get())
                    .build();

            itemShpListRepo.save(itemShoppingList);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Adds the 5 most popular refrigerator items in a shopping list
     *
     * @param shoppingListId the ID of the shopping list
     * @param subUserId the ID of the sub user performing the operation
     * @return if the items were successfully added to the shopping list
     */
    public boolean addMostPopularItems(int shoppingListId, int subUserId) {
        try {
            List<ItemDto> popularItems = refServices.getNMostPopularItems(5);

            for (ItemDto item : popularItems) {

                ItemInShoppingListCreationDto newItem = ItemInShoppingListCreationDto
                        .builder()
                        .itemName(item.getName())
                        .shoppingListId(shoppingListId)
                        .subUserId(subUserId)
                        .amount(1)
                        .measurementType(Measurement.KG)
                        .build();

                if (!itemInShoppinglistExistsWithAccessLevel(shoppingListId, item.getName(),
                        subUserServices.getAccessLevel(subUserId))) {
                    addItemToShoppingList(newItem);
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Checks if the shoppinglist not exist in database by id.
     *
     * @param shoppingListId the shoppinglist id
     * @return true if the shoppinglist exists
     */
    public boolean shoppingListNotExists(int shoppingListId) {
        return !shpListRepo.existsShoppingListByShoppingListId(shoppingListId);
    }

    /**
     * Checks if item exists in shopping list with specific access level
     *
     * @param shoppingListId the shopping list ID
     * @param itemName the item name
     * @param accessLevel the specific access level
     * @return if the item exists
     */
    public boolean itemInShoppinglistExistsWithAccessLevel(int shoppingListId, String itemName, boolean accessLevel) {
        return itemShpListRepo
                .findByItemNameAndShoppingList_ShoppingListIdAndSubUserAccessLevel(
                        itemName,
                        shoppingListId,
                        accessLevel)
                .isPresent();
    }
}