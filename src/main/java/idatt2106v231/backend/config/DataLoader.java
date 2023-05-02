package idatt2106v231.backend.config;

import idatt2106v231.backend.enums.Measurement;
import idatt2106v231.backend.enums.Role;
import idatt2106v231.backend.model.*;
import idatt2106v231.backend.repository.*;
import idatt2106v231.backend.service.RefrigeratorServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    private final CategoryRepository catRepo;
    private final GarbageRepository garbageRepo;
    private final ItemRefrigeratorRepository itemRefRepo;
    private final ItemRepository itemRepo;
    private final RefrigeratorRepository refRepo;
    private final WeekMenuRepository weekMenuRepo;
    private final UserRepository userRepo;
    private final SubUserRepository subUserRepo;
    private final ShoppingListRepository shoppingListRepo;
    private final ItemShoppingListRepository itemShoppingListRepo;
    private final ItemExpirationDateRepository itemExpirationDateRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public DataLoader(CategoryRepository catRepo, GarbageRepository garbageRepo,
                      ItemRefrigeratorRepository itemRefRepo, ItemRepository itemRepo,
                      RefrigeratorRepository refRepo, WeekMenuRepository weekMenuRepo,
                      UserRepository userRepo, SubUserRepository subUserRepo,
                      ShoppingListRepository shoppingListRepo, ItemShoppingListRepository itemShoppingListRepo,
                      ItemExpirationDateRepository itemExpirationDateRepo) {
        this.catRepo = catRepo;
        this.garbageRepo = garbageRepo;
        this.itemRefRepo = itemRefRepo;
        this.itemRepo = itemRepo;
        this.refRepo = refRepo;
        this.weekMenuRepo = weekMenuRepo;
        this.userRepo = userRepo;
        this.subUserRepo = subUserRepo;
        this.shoppingListRepo = shoppingListRepo;
        this.itemShoppingListRepo = itemShoppingListRepo;
        this.itemExpirationDateRepo = itemExpirationDateRepo;
    }

    @Override
    public void run(String... args) throws Exception {
        /*setCategories();
        setUsers();
        setRefrigerators();
        setShoppingLists();
        setWeekMenu();
        setSubUsers();
        setItems();
        setItemsInRefrigerator();
        setItemsInShoppingList();
        setGarbage();
        setItemExpirationDate();*/

    }

    public void setCategories(){
        Category category1 = Category.builder()
                .description("Meieri")
                .build();

        Category category2 = Category.builder()
                .description("Tørrvare")
                .build();

        Category category3 = Category.builder()
                .description("Kjøtt")
                .build();

        catRepo.save(category1);
        catRepo.save(category2);
        catRepo.save(category3);
    }

    public void setUsers() {
        var user1 = User.builder()
                .email("henrik100@stud.ntnu.no")
                .password(passwordEncoder.encode("password"))
                .firstName("Henrik")
                .lastName("Grendal")
                .phoneNumber(99887766)
                .age(58)
                .household(8)
                .role(Role.USER)
                .build();

        var user2 = User.builder()
                .email("frida01@stud.ntnu.no")
                .password(passwordEncoder.encode("password"))
                .firstName("Frida")
                .lastName("Strule")
                .phoneNumber(12345678)
                .age(19)
                .household(4)
                .role(Role.USER)
                .build();

        var user3 = User.builder()
                .email("henriette.eriksen@stud.ntnu.no")
                .password(passwordEncoder.encode("password"))
                .firstName("Henriette")
                .lastName("Eriksen")
                .phoneNumber(22225555)
                .age(25)
                .household(4)
                .role(Role.USER)
                .build();

        var user4 = User.builder()
                .email("pål.einar@stud.ntnu.no")
                .password(passwordEncoder.encode("password"))
                .firstName("Pål Einar")
                .lastName("Borgen")
                .phoneNumber(98765432)
                .age(30)
                .household(1)
                .role(Role.USER)
                .build();

        var user5 = User.builder()
                .email("stig1978@stud.ntnu.no")
                .password(passwordEncoder.encode("password"))
                .firstName("Stig")
                .lastName("Lokøy")
                .phoneNumber(12341234)
                .age(62)
                .household(5)
                .role(Role.USER)
                .build();

        userRepo.save(user1);
        userRepo.save(user2);
        userRepo.save(user3);
        userRepo.save(user4);
        userRepo.save(user5);
    }

    public void setRefrigerators(){
        List<User> users = userRepo.findAll();

        for (User user: users) {
            var ref = Refrigerator.builder()
                    .user(user)
                    .build();
            refRepo.save(ref);
        }
    }

    public void setWeekMenu(){
        List<User> users = userRepo.findAll();

        for (User user: users) {
            var weekMenu = WeeklyMenu.builder()
                    .user(user)
                    .build();
            weekMenuRepo.save(weekMenu);
        }
    }

    public void setShoppingLists(){
        List<User> users = userRepo.findAll();

        for (User user: users) {
            var shoppingList = ShoppingList.builder()
                    .user(user)
                    .build();
            shoppingListRepo.save(shoppingList);
        }
    }

    public void setItems(){
        List<Category> categories = catRepo.findAll();
        Item item1 = Item.builder()
                .name("ost")
                .category(categories.get(0))
                .build();
        Item item2 = Item.builder()
                .name("spaghetti")
                .category(categories.get(1))
                .build();
        Item item3 = Item.builder()
                .name("karbonade")
                .category(categories.get(2))
                .build();
        Item item4 = Item.builder()
                .name("melk")
                .category(categories.get(0))
                .build();
        Item item5 = Item.builder()
                .name("vanilje yoghurt")
                .category(categories.get(0))
                .build();

        itemRepo.save(item1);
        itemRepo.save(item2);
        itemRepo.save(item3);
        itemRepo.save(item4);
        itemRepo.save(item5);
    }

    public void setItemsInRefrigerator(){
        List<Item> items = itemRepo.findAll();
        List<Refrigerator> refrigerators = refRepo.findAll();


        var itemRef1 = ItemRefrigerator.builder()
                .refrigerator(refrigerators.get(0))
                .item(items.get(0))
                .build();

        var itemRef2 = ItemRefrigerator.builder()
                .refrigerator(refrigerators.get(0))
                .item(items.get(1))
                .build();

        var itemRef3 = ItemRefrigerator.builder()
                .refrigerator(refrigerators.get(0))
                .item(items.get(3))
                .build();

        var itemRef4 = ItemRefrigerator.builder()
                .refrigerator(refrigerators.get(1))
                .item(items.get(3))
                .build();

        var itemRef5 = ItemRefrigerator.builder()
                .refrigerator(refrigerators.get(1))
                .item(items.get(4))
                .build();

        var itemRef6 = ItemRefrigerator.builder()
                .refrigerator(refrigerators.get(2))
                .item(items.get(0))
                .build();

        var itemRef7 = ItemRefrigerator.builder()
                .refrigerator(refrigerators.get(2))
                .item(items.get(1))
                .build();

        var itemRef8 = ItemRefrigerator.builder()
                .refrigerator(refrigerators.get(2))
                .item(items.get(2))
                .build();

        var itemRef9 = ItemRefrigerator.builder()
                .refrigerator(refrigerators.get(2))
                .item(items.get(4))
                .build();


        var itemRef10 = ItemRefrigerator.builder()
                .refrigerator(refrigerators.get(3))
                .item(items.get(1))
                .build();

        var itemRef11 = ItemRefrigerator.builder()
                .refrigerator(refrigerators.get(3))
                .item(items.get(2))
                .build();

        var itemRef12 = ItemRefrigerator.builder()
                .refrigerator(refrigerators.get(3))
                .item(items.get(3))
                .build();


        var itemRef13 = ItemRefrigerator.builder()
                .refrigerator(refrigerators.get(4))
                .item(items.get(1))
                .build();

        var itemRef14 = ItemRefrigerator.builder()
                .refrigerator(refrigerators.get(2))
                .item(items.get(3))
                .build();

        itemRefRepo.save(itemRef1);
        itemRefRepo.save(itemRef2);
        itemRefRepo.save(itemRef3);
        itemRefRepo.save(itemRef4);
        itemRefRepo.save(itemRef5);
        itemRefRepo.save(itemRef6);
        itemRefRepo.save(itemRef7);
        itemRefRepo.save(itemRef8);
        itemRefRepo.save(itemRef9);
        itemRefRepo.save(itemRef10);
        itemRefRepo.save(itemRef11);
        itemRefRepo.save(itemRef12);
        itemRefRepo.save(itemRef13);
        itemRefRepo.save(itemRef14);
    }

    public void setItemExpirationDate() throws ParseException {
        List<ItemRefrigerator> itemsInRefrigerator = itemRefRepo.findAll();

        var itemExpirationDate1 = ItemExpirationDate.builder()
                .amount(1.0)
                .measurement(Measurement.UNIT)
                .date(new SimpleDateFormat("yyyy-MM-dd").parse("2023-05-01"))
                .itemRefrigerator(itemsInRefrigerator.get(0))
                .build();

        var itemExpirationDate2 = ItemExpirationDate.builder()
                .amount(600.0)
                .measurement(Measurement.G)
                .date(new SimpleDateFormat("yyyy-MM-dd").parse("2023-05-02"))
                .itemRefrigerator(itemsInRefrigerator.get(1))
                .build();

        var itemExpirationDate3 = ItemExpirationDate.builder()
                .amount(2.0)
                .measurement(Measurement.L)
                .date(new SimpleDateFormat("yyyy-MM-dd").parse("2023-05-03"))
                .itemRefrigerator(itemsInRefrigerator.get(2))
                .build();

        var itemExpirationDate4 = ItemExpirationDate.builder()
                .amount(1.0)
                .measurement(Measurement.UNIT)
                .date(new SimpleDateFormat("yyyy-MM-dd").parse("2023-05-04"))
                .itemRefrigerator(itemsInRefrigerator.get(3))
                .build();

        var itemExpirationDate5 = ItemExpirationDate.builder()
                .amount(750.0)
                .measurement(Measurement.G)
                .date(new SimpleDateFormat("yyyy-MM-dd").parse("2023-05-05"))
                .itemRefrigerator(itemsInRefrigerator.get(4))
                .build();

        var itemExpirationDate6 = ItemExpirationDate.builder()
                .amount(1.0)
                .measurement(Measurement.KG)
                .date(new SimpleDateFormat("yyyy-MM-dd").parse("2023-05-06"))
                .itemRefrigerator(itemsInRefrigerator.get(5))
                .build();

        var itemExpirationDate7 = ItemExpirationDate.builder()
                .amount(1200.0)
                .measurement(Measurement.G)
                .date(new SimpleDateFormat("yyyy-MM-dd").parse("2023-05-07"))
                .itemRefrigerator(itemsInRefrigerator.get(6))
                .build();

        var itemExpirationDate8 = ItemExpirationDate.builder()
                .amount(800.0)
                .measurement(Measurement.G)
                .date(new SimpleDateFormat("yyyy-MM-dd").parse("2023-05-08"))
                .itemRefrigerator(itemsInRefrigerator.get(7))
                .build();

        var itemExpirationDate9 = ItemExpirationDate.builder()
                .amount(3.0)
                .measurement(Measurement.UNIT)
                .date(new SimpleDateFormat("yyyy-MM-dd").parse("2023-05-09"))
                .itemRefrigerator(itemsInRefrigerator.get(8))
                .build();

        var itemExpirationDate10 = ItemExpirationDate.builder()
                .amount(500.0)
                .measurement(Measurement.G)
                .date(new SimpleDateFormat("yyyy-MM-dd").parse("2023-05-10"))
                .itemRefrigerator(itemsInRefrigerator.get(9))
                .build();

        var itemExpirationDate11 = ItemExpirationDate.builder()
                .amount(300.0)
                .measurement(Measurement.G)
                .date(new SimpleDateFormat("yyyy-MM-dd").parse("2023-05-11"))
                .itemRefrigerator(itemsInRefrigerator.get(10))
                .build();

        var itemExpirationDate12 = ItemExpirationDate.builder()
                .amount(2.0)
                .measurement(Measurement.L)
                .date(new SimpleDateFormat("yyyy-MM-dd").parse("2023-05-12"))
                .itemRefrigerator(itemsInRefrigerator.get(11))
                .build();

        var itemExpirationDate13 = ItemExpirationDate.builder()
                .amount(1.0)
                .measurement(Measurement.KG)
                .date(new SimpleDateFormat("yyyy-MM-dd").parse("2023-05-13"))
                .itemRefrigerator(itemsInRefrigerator.get(12))
                .build();

        var itemExpirationDate14 = ItemExpirationDate.builder()
                .amount(600)
                .measurement(Measurement.G)
                .itemRefrigerator(itemsInRefrigerator.get(13))
                .build();

        itemExpirationDateRepo.save(itemExpirationDate1);
        itemExpirationDateRepo.save(itemExpirationDate2);
        itemExpirationDateRepo.save(itemExpirationDate3);
        itemExpirationDateRepo.save(itemExpirationDate4);
        itemExpirationDateRepo.save(itemExpirationDate5);
        itemExpirationDateRepo.save(itemExpirationDate6);
        itemExpirationDateRepo.save(itemExpirationDate7);
        itemExpirationDateRepo.save(itemExpirationDate8);
        itemExpirationDateRepo.save(itemExpirationDate9);
        itemExpirationDateRepo.save(itemExpirationDate10);
        itemExpirationDateRepo.save(itemExpirationDate11);
        itemExpirationDateRepo.save(itemExpirationDate12);
        itemExpirationDateRepo.save(itemExpirationDate13);
        itemExpirationDateRepo.save(itemExpirationDate14);

    }

    public void setItemsInShoppingList(){
        List<Item> items = itemRepo.findAll();
        List<ShoppingList> shoppingLists = shoppingListRepo.findAll();

        var itemShoppingList1 = ItemShoppingList.builder()
                .amount(1)
                .measurement(Measurement.KG)
                .shoppingList(shoppingLists.get(0))
                .item(items.get(0))
                .build();

        var itemShoppingList2 = ItemShoppingList.builder()
                .amount(1)
                .measurement(Measurement.L)
                .shoppingList(shoppingLists.get(0))
                .item(items.get(3))
                .build();

        var itemShoppingList3 = ItemShoppingList.builder()
                .amount(1)
                .measurement(Measurement.KG)
                .shoppingList(shoppingLists.get(1))
                .item(items.get(1))
                .build();

        var itemShoppingList4 = ItemShoppingList.builder()
                .amount(2)
                .measurement(Measurement.L)
                .shoppingList(shoppingLists.get(2))
                .item(items.get(4))
                .build();

        var itemShoppingList5 = ItemShoppingList.builder()
                .amount(3)
                .measurement(Measurement.L)
                .shoppingList(shoppingLists.get(3))
                .item(items.get(2))
                .build();

        var itemShoppingList6 = ItemShoppingList.builder()
                .amount(2)
                .measurement(Measurement.L)
                .shoppingList(shoppingLists.get(4))
                .item(items.get(3))
                .build();

        var itemShoppingList7 = ItemShoppingList.builder()
                .amount(1)
                .measurement(Measurement.L)
                .shoppingList(shoppingLists.get(0))
                .item(items.get(1))
                .build();

        itemShoppingListRepo.save(itemShoppingList1);
        itemShoppingListRepo.save(itemShoppingList2);
        itemShoppingListRepo.save(itemShoppingList3);
        itemShoppingListRepo.save(itemShoppingList4);
        itemShoppingListRepo.save(itemShoppingList5);
        itemShoppingListRepo.save(itemShoppingList6);
        itemShoppingListRepo.save(itemShoppingList7);

    }

    public void setSubUsers(){
        List<User> userList = userRepo.findAll();

        var subUser1 = SubUser.builder()
                .accessLevel(true)
                .name("Torstein")
                .masterUser(userList.get(0))
                .build();

        var subUser2 = SubUser.builder()
                .accessLevel(false)
                .name("Ida")
                .masterUser(userList.get(0))
                .build();

        var subUser3 = SubUser.builder()
                .accessLevel(true)
                .name("Helene")
                .masterUser(userList.get(1))
                .build();

        var subUser4 = SubUser.builder()
                .accessLevel(false)
                .name("Nils")
                .masterUser(userList.get(2))
                .build();

        var subUser5 = SubUser.builder()
                .accessLevel(true)
                .name("Tim")
                .masterUser(userList.get(4))
                .build();

        var subUser6 = SubUser.builder()
                .accessLevel(true)
                .name("Ella")
                .masterUser(userList.get(4))
                .build();

        subUserRepo.save(subUser1);
        subUserRepo.save(subUser2);
        subUserRepo.save(subUser3);
        subUserRepo.save(subUser4);
        subUserRepo.save(subUser5);
        subUserRepo.save(subUser6);


    }

    public void setGarbage(){
        List<Refrigerator> refrigerators = refRepo.findAll();

        for (Refrigerator refrigerator: refrigerators) {
            Garbage garbage = Garbage.builder()
                    .refrigerator(refrigerator)
                    .amount(50)
                    .build();
            garbageRepo.save(garbage);
        }


    }
}