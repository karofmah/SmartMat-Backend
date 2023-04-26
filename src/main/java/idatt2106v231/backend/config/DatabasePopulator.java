package idatt2106v231.backend.config;

import idatt2106v231.backend.dto.item.ItemDto;
import idatt2106v231.backend.enums.Role;
import idatt2106v231.backend.model.Category;
import idatt2106v231.backend.model.Item;
import idatt2106v231.backend.model.User;
import idatt2106v231.backend.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DatabasePopulator implements CommandLineRunner {

    private final CategoryRepository catRepo;
    private final GarbageRepository garbageRepo;
    private final ItemRepository itemRefRepo;
    private final ItemRepository itemRepo;
    private final RefrigeratorRepository refRepo;
    private final WeekMenuRepository weekMenuRepo;
    private final UserRepository userRepo;

    private PasswordEncoder passwordEncoder;

    public DatabasePopulator(CategoryRepository catRepo, GarbageRepository garbageRepo,
                             ItemRepository itemRefRepo, ItemRepository itemRepo,
                             RefrigeratorRepository refRepo, WeekMenuRepository weekMenuRepo,
                             UserRepository userRepo) {
        this.catRepo = catRepo;
        this.garbageRepo = garbageRepo;
        this.itemRefRepo = itemRefRepo;
        this.itemRepo = itemRepo;
        this.refRepo = refRepo;
        this.weekMenuRepo = weekMenuRepo;
        this.userRepo = userRepo;
    }

    @Override
    public void run(String... args) throws Exception {
        setCategories();
        setUsers();
        setItems();
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

    }
}