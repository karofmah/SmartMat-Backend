package idatt2106v231.backend.config;

import idatt2106v231.backend.enums.Measurement;
import idatt2106v231.backend.enums.Role;
import idatt2106v231.backend.model.*;
import idatt2106v231.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

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

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DataLoader(CategoryRepository catRepo, GarbageRepository garbageRepo,
                      ItemRefrigeratorRepository itemRefRepo, ItemRepository itemRepo,
                      RefrigeratorRepository refRepo, WeekMenuRepository weekMenuRepo,
                      UserRepository userRepo, SubUserRepository subUserRepo,
                      ShoppingListRepository shoppingListRepo, ItemShoppingListRepository itemShoppingListRepo,
                      PasswordEncoder passwordEncoder, ItemExpirationDateRepository itemExpirationDateRepo) {
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
        this.passwordEncoder = passwordEncoder;
        this.itemExpirationDateRepo = itemExpirationDateRepo;
    }

    @Override
    public void run(String... args) throws Exception {
        /*  setUsers();
        setRefrigerators();
        setShoppingLists();
        setWeekMenu();
        setGarbage();
        setSubUsers();

        setItemsInRefrigerator();
        setItemsInShoppingList();
        setItemExpirationDate();

       */
    }

    public void setUsers() {

        var user1 = User.builder()
                .email("andersandersen@stud.ntnu.no")
                .password(passwordEncoder.encode("password"))
                .firstName("Anders")
                .lastName("Andersen")
                .phoneNumber(77777777)
                .age(41)
                .household(3)
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
                .email("henrik100@stud.ntnu.no")
                .password(passwordEncoder.encode("password"))
                .firstName("Henrik")
                .lastName("Grendal")
                .phoneNumber(99887766)
                .age(58)
                .household(8)
                .role(Role.USER)
                .build();

        var user5 = User.builder()
                .email("kari-hansen@stud.ntnu.no")
                .password(passwordEncoder.encode("password"))
                .firstName("Kari")
                .lastName("Hansen")
                .phoneNumber(44444444)
                .age(52)
                .household(2)
                .role(Role.USER)
                .build();

        var user6 = User.builder()
                .email("lise-sivertsen@stud.ntnu.no")
                .password(passwordEncoder.encode("password"))
                .firstName("Lise")
                .lastName("Sivertsen")
                .phoneNumber(55555555)
                .age(28)
                .household(1)
                .role(Role.USER)
                .build();

        var user7 = User.builder()
                .email("marie1986@stud.ntnu.no")
                .password(passwordEncoder.encode("password"))
                .firstName("Marie")
                .lastName("Andersen")
                .phoneNumber(99999999)
                .age(35)
                .household(3)
                .role(Role.USER)
                .build();

        var user8 = User.builder()
                .email("pål.einar@stud.ntnu.no")
                .password(passwordEncoder.encode("password"))
                .firstName("Pål Einar")
                .lastName("Borgen")
                .phoneNumber(98765432)
                .age(30)
                .household(1)
                .role(Role.USER)
                .build();

        var user9 = User.builder()
                .email("per.pettersen@stud.ntnu.no")
                .password(passwordEncoder.encode("password"))
                .firstName("Per")
                .lastName("Pettersen")
                .phoneNumber(11111111)
                .age(45)
                .household(2)
                .role(Role.USER)
                .build();

        var user10 = User.builder()
                .email("stig1978@stud.ntnu.no")
                .password(passwordEncoder.encode("password"))
                .firstName("Stig")
                .lastName("Lokøy")
                .phoneNumber(12341234)
                .age(62)
                .household(5)
                .role(Role.USER)
                .build();

        userRepo.saveAll(Arrays.asList(user1, user2,
                user3, user4, user5, user6,
                user7, user8, user9, user10));
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

    public void setGarbage(){
        Random random = new Random();
        List<Refrigerator> refrigerators = refRepo.findAll();

        for (int i = 2020; i <= 2022; i++){
            for (int j = 1; j <= 12; j++){
                for (Refrigerator refrigerator: refrigerators) {
                    Garbage garbage = Garbage.builder()
                            .refrigerator(refrigerator)
                            .amount(random.nextInt(5) + 10)
                            .date(YearMonth.of(i, j))
                            .build();
                    garbageRepo.save(garbage);
                }
            }
        }

        for (int j = 1; j <= 4; j++){
            for (Refrigerator refrigerator: refrigerators) {
                Garbage garbage = Garbage.builder()
                        .refrigerator(refrigerator)
                        .amount(random.nextInt(5) + 10)
                        .date(YearMonth.of(2023, j))
                        .build();
                garbageRepo.save(garbage);
            }
        }

        for (Refrigerator refrigerator: refrigerators) {
            Garbage garbage = Garbage.builder()
                    .refrigerator(refrigerator)
                    .amount(random.nextInt(0) + 5)
                    .date(YearMonth.of(2023, 4))
                    .build();
            garbageRepo.save(garbage);
        }
    }

    public void setSubUsers(){
        List<User> userList = userRepo.findAll();

        var subUser1 = SubUser.builder()
                .accessLevel(true)
                .name("Henrik")
                .user(userList.get(0))
                .pinCode(1234)
                .build();

        var subUser2 = SubUser.builder()
                .accessLevel(true)
                .name("Ida")
                .pinCode(1234)
                .user(userList.get(0))
                .build();

        var subUser3 = SubUser.builder()
                .accessLevel(false)
                .name("Helene")
                .user(userList.get(0))
                .build();

        var subUser4 = SubUser.builder()
                .accessLevel(true)
                .name("Frida")
                .user(userList.get(1))
                .pinCode(1234)
                .build();

        var subUser5 = SubUser.builder()
                .accessLevel(false)
                .name("Tim")
                .user(userList.get(1))
                .build();

        var subUser6 = SubUser.builder()
                .accessLevel(false)
                .name("Ella")
                .user(userList.get(1))
                .build();

        var subUser7 = SubUser.builder()
                .accessLevel(true)
                .name("Henriette")
                .user(userList.get(2))
                .pinCode(1234)
                .build();

        var subUser8 = SubUser.builder()
                .accessLevel(false)
                .name("Ingrid")
                .user(userList.get(2))
                .build();

        var subUser9 = SubUser.builder()
                .accessLevel(true)
                .name("Marius")
                .user(userList.get(2))
                .pinCode(1234)
                .build();

        var subUser10 = SubUser.builder()
                .accessLevel(true)
                .name("Pål Einar")
                .user(userList.get(3))
                .pinCode(1234)
                .build();

        var subUser11 = SubUser.builder()
                .accessLevel(false)
                .name("Tore")
                .user(userList.get(3))
                .build();

        var subUser12 = SubUser.builder()
                .accessLevel(false)
                .name("Hans")
                .user(userList.get(3))
                .build();

        var subUser13 = SubUser.builder()
                .accessLevel(true)
                .name("Stig")
                .user(userList.get(4))
                .pinCode(1234)
                .build();

        var subUser14 = SubUser.builder()
                .accessLevel(false)
                .name("Per")
                .user(userList.get(4))
                .build();

        var subUser15 = SubUser.builder()
                .accessLevel(false)
                .name("Marie")
                .user(userList.get(4))
                .build();

        var subUser16 = SubUser.builder()
                .accessLevel(true)
                .name("Marie")
                .user(userList.get(5))
                .pinCode(1234)
                .build();

        var subUser17 = SubUser.builder()
                .accessLevel(false)
                .name("Kine")
                .user(userList.get(5))
                .build();

        var subUser18 = SubUser.builder()
                .accessLevel(true)
                .name("Espen")
                .user(userList.get(5))
                .pinCode(1234)
                .build();

        var subUser19 = SubUser.builder()
                .accessLevel(true)
                .name("Per")
                .user(userList.get(6))
                .pinCode(1234)
                .build();

        var subUser20 = SubUser.builder()
                .accessLevel(false)
                .name("Ole")
                .user(userList.get(6))
                .build();

        var subUser21 = SubUser.builder()
                .accessLevel(true)
                .name("Anna")
                .user(userList.get(6))
                .pinCode(1234)
                .build();

        var subUser22 = SubUser.builder()
                .accessLevel(true)
                .name("Lise")
                .user(userList.get(7))
                .pinCode(1234)
                .build();

        var subUser23 = SubUser.builder()
                .accessLevel(false)
                .name("Trine")
                .user(userList.get(7))
                .build();

        var subUser24 = SubUser.builder()
                .accessLevel(false)
                .name("Kjell")
                .user(userList.get(7))
                .build();

        var subUser25 = SubUser.builder()
                .accessLevel(true)
                .name("Kari")
                .user(userList.get(8))
                .pinCode(1234)
                .build();

        var subUser26 = SubUser.builder()
                .accessLevel(true)
                .name("Hanne")
                .user(userList.get(8))
                .pinCode(1234)
                .build();

        var subUser27 = SubUser.builder()
                .accessLevel(true)
                .name("Erik")
                .user(userList.get(8))
                .pinCode(1234)
                .build();

        var subUser28 = SubUser.builder()
                .accessLevel(true)
                .name("Anders")
                .user(userList.get(9))
                .pinCode(1234)
                .build();

        var subUser29 = SubUser.builder()
                .accessLevel(false)
                .name("Svein")
                .user(userList.get(9))
                .build();

        var subUser30 = SubUser.builder()
                .accessLevel(false)
                .name("Anne")
                .user(userList.get(9))
                .build();


        subUserRepo.saveAll(Arrays.asList(subUser1,
                subUser2, subUser3, subUser4, subUser5,
                subUser5, subUser6,subUser7, subUser8,
                subUser9, subUser10, subUser11, subUser12,
                subUser13, subUser14, subUser15, subUser16,
                subUser17, subUser18, subUser19, subUser20,
                subUser21, subUser22, subUser23, subUser24,
                subUser25, subUser26, subUser27, subUser28,
                subUser29, subUser30));
    }

    public void setItemsInRefrigerator(){
        List<Item> items = itemRepo.findAll();
        List<Refrigerator> refrigerators = refRepo.findAll();

        var itemRef1 = ItemRefrigerator.builder()
                .refrigerator(refrigerators.get(0))
                .item(items.get(0))
                .measurementType(Measurement.KG)
                .build();

        var itemRef2 = ItemRefrigerator.builder()
                .refrigerator(refrigerators.get(0))
                .item(items.get(1))
                .measurementType(Measurement.KG)
                .build();

        var itemRef3 = ItemRefrigerator.builder()
                .refrigerator(refrigerators.get(0))
                .item(items.get(3))
                .measurementType(Measurement.DL)
                .build();

        var itemRef4 = ItemRefrigerator.builder()
                .refrigerator(refrigerators.get(1))
                .item(items.get(3))
                .measurementType(Measurement.DL)
                .build();

        var itemRef5 = ItemRefrigerator.builder()
                .refrigerator(refrigerators.get(1))
                .item(items.get(4))
                .measurementType(Measurement.G)
                .build();

        var itemRef6 = ItemRefrigerator.builder()
                .refrigerator(refrigerators.get(2))
                .item(items.get(0))
                .measurementType(Measurement.KG)
                .build();

        var itemRef7 = ItemRefrigerator.builder()
                .refrigerator(refrigerators.get(2))
                .item(items.get(1))
                .measurementType(Measurement.KG)
                .build();

        var itemRef8 = ItemRefrigerator.builder()
                .refrigerator(refrigerators.get(2))
                .item(items.get(2))
                .measurementType(Measurement.KG)
                .build();

        var itemRef9 = ItemRefrigerator.builder()
                .refrigerator(refrigerators.get(2))
                .item(items.get(4))
                .measurementType(Measurement.G)
                .build();


        var itemRef10 = ItemRefrigerator.builder()
                .refrigerator(refrigerators.get(3))
                .item(items.get(1))
                .measurementType(Measurement.KG)
                .build();

        var itemRef11 = ItemRefrigerator.builder()
                .refrigerator(refrigerators.get(3))
                .item(items.get(2))
                .measurementType(Measurement.KG)
                .build();

        var itemRef12 = ItemRefrigerator.builder()
                .refrigerator(refrigerators.get(3))
                .item(items.get(3))
                .measurementType(Measurement.DL)
                .build();


        var itemRef13 = ItemRefrigerator.builder()
                .refrigerator(refrigerators.get(4))
                .item(items.get(1))
                .measurementType(Measurement.KG)
                .build();

        var itemRef14 = ItemRefrigerator.builder()
                .refrigerator(refrigerators.get(2))
                .item(items.get(3))
                .measurementType(Measurement.DL)
                .build();

        var itemRef15 = ItemRefrigerator.builder()
                .refrigerator(refrigerators.get(4))
                .item(items.get(0))
                .measurementType(Measurement.L)
                .build();

        var itemRef16 = ItemRefrigerator.builder()
                .refrigerator(refrigerators.get(1))
                .item(items.get(2))
                .measurementType(Measurement.L)
                .build();

        var itemRef17 = ItemRefrigerator.builder()
                .refrigerator(refrigerators.get(0))
                .item(items.get(4))
                .measurementType(Measurement.DL)
                .build();

        var itemRef18 = ItemRefrigerator.builder()
                .refrigerator(refrigerators.get(3))
                .item(items.get(4))
                .measurementType(Measurement.KG)
                .build();

        var itemRef19 = ItemRefrigerator.builder()
                .refrigerator(refrigerators.get(2))
                .item(items.get(5))
                .measurementType(Measurement.G)
                .build();

        var itemRef20 = ItemRefrigerator.builder()
                .refrigerator(refrigerators.get(4))
                .item(items.get(3))
                .measurementType(Measurement.L)
                .build();

        itemRefRepo.saveAll(Arrays.asList(
                itemRef1, itemRef2, itemRef3, itemRef4,
                itemRef5, itemRef6, itemRef7, itemRef8,
                itemRef9, itemRef10, itemRef11, itemRef12,
                itemRef13, itemRef14, itemRef15, itemRef16,
                itemRef17, itemRef18, itemRef19, itemRef20
        ));
    }

    public void setItemExpirationDate() throws ParseException {
        List<ItemRefrigerator> itemsInRefrigerator = itemRefRepo.findAll();

        var itemExpirationDate1 = ItemExpirationDate.builder()
                .amount(1.0)
                .date(new SimpleDateFormat("yyyy-MM-dd").parse("2023-05-01"))
                .itemRefrigerator(itemsInRefrigerator.get(0))
                .build();

        var itemExpirationDate2 = ItemExpirationDate.builder()
                .amount(600.0)
                .date(new SimpleDateFormat("yyyy-MM-dd").parse("2023-05-02"))
                .itemRefrigerator(itemsInRefrigerator.get(1))
                .build();

        var itemExpirationDate3 = ItemExpirationDate.builder()
                .amount(2.0)
                .date(new SimpleDateFormat("yyyy-MM-dd").parse("2023-05-03"))
                .itemRefrigerator(itemsInRefrigerator.get(2))
                .build();

        var itemExpirationDate4 = ItemExpirationDate.builder()
                .amount(1.0)
                .date(new SimpleDateFormat("yyyy-MM-dd").parse("2023-05-04"))
                .itemRefrigerator(itemsInRefrigerator.get(3))
                .build();

        var itemExpirationDate5 = ItemExpirationDate.builder()
                .amount(750.0)
                .date(new SimpleDateFormat("yyyy-MM-dd").parse("2023-05-05"))
                .itemRefrigerator(itemsInRefrigerator.get(4))
                .build();

        var itemExpirationDate6 = ItemExpirationDate.builder()
                .amount(1.0)
                .date(new SimpleDateFormat("yyyy-MM-dd").parse("2023-05-06"))
                .itemRefrigerator(itemsInRefrigerator.get(5))
                .build();

        var itemExpirationDate7 = ItemExpirationDate.builder()
                .amount(1200.0)
                .date(new SimpleDateFormat("yyyy-MM-dd").parse("2023-05-07"))
                .itemRefrigerator(itemsInRefrigerator.get(6))
                .build();

        var itemExpirationDate8 = ItemExpirationDate.builder()
                .amount(800.0)
                .date(new SimpleDateFormat("yyyy-MM-dd").parse("2023-05-08"))
                .itemRefrigerator(itemsInRefrigerator.get(7))
                .build();

        var itemExpirationDate9 = ItemExpirationDate.builder()
                .amount(3.0)
                .date(new SimpleDateFormat("yyyy-MM-dd").parse("2023-05-09"))
                .itemRefrigerator(itemsInRefrigerator.get(8))
                .build();

        var itemExpirationDate10 = ItemExpirationDate.builder()
                .amount(500.0)
                .date(new SimpleDateFormat("yyyy-MM-dd").parse("2023-05-10"))
                .itemRefrigerator(itemsInRefrigerator.get(9))
                .build();

        var itemExpirationDate11 = ItemExpirationDate.builder()
                .amount(300.0)
                .date(new SimpleDateFormat("yyyy-MM-dd").parse("2023-05-11"))
                .itemRefrigerator(itemsInRefrigerator.get(10))
                .build();

        var itemExpirationDate12 = ItemExpirationDate.builder()
                .amount(2.0)
                .date(new SimpleDateFormat("yyyy-MM-dd").parse("2023-05-12"))
                .itemRefrigerator(itemsInRefrigerator.get(11))
                .build();

        var itemExpirationDate13 = ItemExpirationDate.builder()
                .amount(1.0)
                .date(new SimpleDateFormat("yyyy-MM-dd").parse("2023-05-13"))
                .itemRefrigerator(itemsInRefrigerator.get(12))
                .build();

        var itemExpirationDate14 = ItemExpirationDate.builder()
                .amount(600)
                .itemRefrigerator(itemsInRefrigerator.get(13))
                .build();

        var itemExpirationDate15 = ItemExpirationDate.builder()
                .amount(400.0)
                .date(new SimpleDateFormat("yyyy-MM-dd").parse("2023-05-14"))
                .itemRefrigerator(itemsInRefrigerator.get(14))
                .build();

        var itemExpirationDate16 = ItemExpirationDate.builder()
                .amount(200.0)
                .date(new SimpleDateFormat("yyyy-MM-dd").parse("2023-05-15"))
                .itemRefrigerator(itemsInRefrigerator.get(15))
                .build();

        var itemExpirationDate17 = ItemExpirationDate.builder()
                .amount(100.0)
                .date(new SimpleDateFormat("yyyy-MM-dd").parse("2023-05-16"))
                .itemRefrigerator(itemsInRefrigerator.get(16))
                .build();

        var itemExpirationDate18 = ItemExpirationDate.builder()
                .amount(800.0)
                .date(new SimpleDateFormat("yyyy-MM-dd").parse("2023-05-17"))
                .itemRefrigerator(itemsInRefrigerator.get(17))
                .build();

        var itemExpirationDate19 = ItemExpirationDate.builder()
                .amount(1.5)
                .date(new SimpleDateFormat("yyyy-MM-dd").parse("2023-05-18"))
                .itemRefrigerator(itemsInRefrigerator.get(18))
                .build();

        var itemExpirationDate20 = ItemExpirationDate.builder()
                .amount(500.0)
                .date(new SimpleDateFormat("yyyy-MM-dd").parse("2023-05-19"))
                .itemRefrigerator(itemsInRefrigerator.get(19))
                .build();

        var itemExpirationDate21 = ItemExpirationDate.builder()
                .amount(250.0)
                .date(new SimpleDateFormat("yyyy-MM-dd").parse("2023-05-20"))
                .itemRefrigerator(itemsInRefrigerator.get(20))
                .build();

        var itemExpirationDate22 = ItemExpirationDate.builder()
                .amount(150.0)
                .date(new SimpleDateFormat("yyyy-MM-dd").parse("2023-05-21"))
                .itemRefrigerator(itemsInRefrigerator.get(21))
                .build();

        var itemExpirationDate23 = ItemExpirationDate.builder()
                .amount(400.0)
                .date(new SimpleDateFormat("yyyy-MM-dd").parse("2023-05-22"))
                .itemRefrigerator(itemsInRefrigerator.get(22))
                .build();

        var itemExpirationDate24 = ItemExpirationDate.builder()
                .amount(100.0)
                .date(new SimpleDateFormat("yyyy-MM-dd").parse("2023-05-23"))
                .itemRefrigerator(itemsInRefrigerator.get(23))
                .build();

        var itemExpirationDate25 = ItemExpirationDate.builder()
                .amount(600.0)
                .date(new SimpleDateFormat("yyyy-MM-dd").parse("2023-05-24"))
                .itemRefrigerator(itemsInRefrigerator.get(24))
                .build();

        var itemExpirationDate26 = ItemExpirationDate.builder()
                .amount(350.0)
                .date(new SimpleDateFormat("yyyy-MM-dd").parse("2023-05-25"))
                .itemRefrigerator(itemsInRefrigerator.get(25))
                .build();

        var itemExpirationDate27 = ItemExpirationDate.builder()
                .amount(200.0)
                .date(new SimpleDateFormat("yyyy-MM-dd").parse("2023-05-26"))
                .itemRefrigerator(itemsInRefrigerator.get(26))
                .build();

        var itemExpirationDate28 = ItemExpirationDate.builder()
                .amount(1.0)
                .date(new SimpleDateFormat("yyyy-MM-dd").parse("2023-05-27"))
                .itemRefrigerator(itemsInRefrigerator.get(27))
                .build();

        var itemExpirationDate29 = ItemExpirationDate.builder()
                .amount(400.0)
                .date(new SimpleDateFormat("yyyy-MM-dd").parse("2023-05-28"))
                .itemRefrigerator(itemsInRefrigerator.get(28))
                .build();

        var itemExpirationDate30 = ItemExpirationDate.builder()
                .amount(500.0)
                .date(new SimpleDateFormat("yyyy-MM-dd").parse("2023-05-29"))
                .itemRefrigerator(itemsInRefrigerator.get(29))
                .build();


        itemExpirationDateRepo.saveAll(Arrays.asList(
                itemExpirationDate1, itemExpirationDate2,
                itemExpirationDate3, itemExpirationDate4,
                itemExpirationDate5, itemExpirationDate6,
                itemExpirationDate7, itemExpirationDate8,
                itemExpirationDate9, itemExpirationDate10,
                itemExpirationDate11, itemExpirationDate12,
                itemExpirationDate13, itemExpirationDate14,
                itemExpirationDate15, itemExpirationDate16,
                itemExpirationDate17, itemExpirationDate18,
                itemExpirationDate19, itemExpirationDate20,
                itemExpirationDate21, itemExpirationDate22,
                itemExpirationDate23, itemExpirationDate24,
                itemExpirationDate25, itemExpirationDate26,
                itemExpirationDate27, itemExpirationDate28,
                itemExpirationDate29, itemExpirationDate30
        ));
    }

    public void setItemsInShoppingList(){
        List<Item> items = itemRepo.findAll();
        List<ShoppingList> shoppingLists = shoppingListRepo.findAll();
        List<SubUser> subUserList = subUserRepo.findAll();

        var itemShoppingList1 = ItemShoppingList.builder()
                .amount(1)
                .measurementType(Measurement.KG)
                .shoppingList(shoppingLists.get(0))
                .item(items.get(0))
                .subUser(subUserList.get(0))
                .build();

        var itemShoppingList2 = ItemShoppingList.builder()
                .amount(1)
                .measurementType(Measurement.L)
                .shoppingList(shoppingLists.get(0))
                .item(items.get(3))
                .subUser(subUserList.get(1))
                .build();

        var itemShoppingList3 = ItemShoppingList.builder()
                .amount(1)
                .measurementType(Measurement.KG)
                .shoppingList(shoppingLists.get(1))
                .item(items.get(1))
                .subUser(subUserList.get(2))
                .build();

        var itemShoppingList4 = ItemShoppingList.builder()
                .amount(2)
                .measurementType(Measurement.L)
                .shoppingList(shoppingLists.get(2))
                .item(items.get(4))
                .subUser(subUserList.get(3))
                .build();

        var itemShoppingList5 = ItemShoppingList.builder()
                .amount(3)
                .measurementType(Measurement.L)
                .shoppingList(shoppingLists.get(3))
                .item(items.get(2))
                .subUser(subUserList.get(4))
                .build();

        var itemShoppingList6 = ItemShoppingList.builder()
                .amount(2)
                .measurementType(Measurement.L)
                .shoppingList(shoppingLists.get(4))
                .item(items.get(3))
                .subUser(subUserList.get(5))
                .build();

        var itemShoppingList7 = ItemShoppingList.builder()
                .amount(1)
                .measurementType(Measurement.L)
                .shoppingList(shoppingLists.get(0))
                .item(items.get(1))
                .subUser(subUserList.get(5))
                .build();

        itemShoppingListRepo.save(itemShoppingList1);
        itemShoppingListRepo.save(itemShoppingList2);
        itemShoppingListRepo.save(itemShoppingList3);
        itemShoppingListRepo.save(itemShoppingList4);
        itemShoppingListRepo.save(itemShoppingList5);
        itemShoppingListRepo.save(itemShoppingList6);
        itemShoppingListRepo.save(itemShoppingList7);

    }
}