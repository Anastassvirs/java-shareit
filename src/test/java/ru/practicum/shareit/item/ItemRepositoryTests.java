package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRepositoryTests {
    private final ItemRepository itemRepository;

    @Test
    void findAllByTextTest() {
        Long itemId = 1L;
        Long itemId2 = 2L;
        Long itemId3 = 3L;
        Integer from = 0;
        Integer size = 3;
        String text = "eas";
        Pageable pageable = PageRequest.of(from / size, size);
        Item item = new Item(itemId, "Name", "Some description", true);
        Item item2 = new Item(itemId2, "hehehe", "pleaseHelp", true);
        Item item3 = new Item(itemId3, "Yan", "EasyPeasy", true);
        itemRepository.save(item);
        itemRepository.save(item2);
        itemRepository.save(item3);
        List<Item> items = itemRepository.findAllByText(text, pageable);

        assertEquals(List.of(item2, item3), items);
    }
}
