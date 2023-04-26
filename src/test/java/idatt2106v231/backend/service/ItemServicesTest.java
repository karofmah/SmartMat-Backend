package idatt2106v231.backend.service;

import idatt2106v231.backend.dto.item.ItemDto;
import idatt2106v231.backend.model.Garbage;
import idatt2106v231.backend.model.Item;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

class ItemServicesTest {

    ModelMapper mapper = new ModelMapper();

    RefrigeratorServices services = new RefrigeratorServices();


    @Test
    public void saveItem() {
        ItemDto itemDto = new ItemDto("ost", 1);
        Item item = mapper.map(itemDto, Item.class);

    }

    @Test
    public void test() {
        Garbage garbage = new Garbage();

        System.out.println(garbage.getYear());
    }


}