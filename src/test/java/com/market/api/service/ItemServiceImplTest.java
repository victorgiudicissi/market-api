package com.market.api.service;

import com.market.api.dto.item.*;
import com.market.api.exception.DataNotFoundException;
import com.market.api.entity.Item;
import com.market.api.repository.ItemRepository;
import com.market.api.service.impl.ItemServiceImpl;
import com.market.api.service.impl.KafkaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

public class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private MongoTemplate mongoTemplate;

    @Mock
    private KafkaServiceImpl kafkaService;

    @InjectMocks
    private ItemServiceImpl itemService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void save_WhenItemsAreAvailableAndInStock_ShouldReturnCartResponseDto() {
        // GIVEN
        Item data = Item
                .builder()
                .marketUuid("mock-market-uuid")
                .description("Mock item")
                .enabled(true)
                .quantity(100L)
                .price(110L)
                .build();

        when(itemRepository.save(any())).thenReturn(data);
        doNothing().when(kafkaService).sendEvent(any(), any());

        // WHEN
        Item result = itemService.save(data);

        // THEN
        assertNotNull(result);
        assertEquals("mock-market-uuid", result.getMarketUuid());
        assertEquals(110L, result.getPrice());
    }

    @Test
    void updateItem_WhenItemExists_ShouldUpdateItem() {
        // GIVEN
        Item data = Item
                .builder()
                .marketUuid("mock-market-uuid")
                .description("Mock item")
                .enabled(true)
                .quantity(100L)
                .price(110L)
                .build();

        when(itemRepository.findById(any())).thenReturn(Optional.of(data));
        when(itemRepository.save(any())).thenReturn(data);
        doNothing().when(kafkaService).sendEvent(any(), any());

        // WHEN
        Item result = itemService.updateItem("mock-uuid", data);

        // THEN
        assertNotNull(result);
        assertEquals("mock-market-uuid", result.getMarketUuid());
        assertEquals(110L, result.getPrice());
        verify(itemRepository, times(1)).findById(any());
    }

    @Test
    void updateItem_WhenItemDoesNotExists_ShouldThrowDataNotFoundException() {
        // GIVEN
        Item data = Item
                .builder()
                .marketUuid("mock-market-uuid")
                .description("Mock item")
                .enabled(true)
                .quantity(100L)
                .price(110L)
                .build();

        when(itemRepository.findById(any())).thenReturn(Optional.empty());
        when(itemRepository.save(any())).thenReturn(data);
        doNothing().when(kafkaService).sendEvent(any(), any());

        // WHEN - THEN
        assertThrows(DataNotFoundException.class, () -> itemService.updateItem("mock-uuid", data));

        verify(itemRepository, times(1)).findById(any());
        verify(itemRepository, times(0)).save(any());

    }

    @Test
    void findItemByUuid_WhenItemExists_ShouldUpdateItem() {
        // GIVEN
        ItemRequestDto itemRequestDto = ItemRequestDto
                .builder()
                .marketUuid("mock-market-uuid")
                .description("Mock item")
                .enabled(true)
                .quantity(100L)
                .price(110L)
                .build();

        when(itemRepository.findById(any())).thenReturn(Optional.of(itemRequestDto.toItem()));

        // WHEN
        Item result = itemService.findItemByUuid("mock-uuid");

        // THEN
        assertNotNull(result);
        assertEquals("mock-market-uuid", result.getMarketUuid());
        assertEquals(110L, result.getPrice());
        verify(itemRepository, times(1)).findById(any());
    }

    @Test
    void findItemByFilter_WhenItemIsFound_ShouldReturnItem() {
        // GIVEN
        FilterItemRequestDto filter = FilterItemRequestDto.builder()
                .marketUuid("market-uuid")
                .description("Test")
                .enabled(true)
                .page(1)
                .size(10)
                .build();

        Item item = Item
                .builder()
                .marketUuid("mock-market-uuid")
                .quantity(100L)
                .price(100L)
                .build();

        when(mongoTemplate.count(any(), anyString())).thenReturn(1L);
        when(mongoTemplate.find(any(), any())).thenReturn(List.of(item));

        // WHEN
        List<Item> result = itemService.findItemsByFilter(filter);

        // THEN
        assertEquals(1, result.size());
    }

    @Test
    void deleteItem_WhenItemExists_ShouldDeleteItem() {
        // GIVEN
        ItemRequestDto itemRequestDto = ItemRequestDto
                .builder()
                .marketUuid("mock-market-uuid")
                .description("Mock item")
                .enabled(true)
                .quantity(100L)
                .price(110L)
                .build();

        when(itemRepository.findById(any())).thenReturn(Optional.of(itemRequestDto.toItem()));
        doNothing().when(itemRepository).delete(any());
        doNothing().when(kafkaService).sendEvent(any(), any());

        // WHEN
        itemService.delete("mock-uuid");

        // THEN
        verify(itemRepository, times(1)).findById(any());
        verify(itemRepository, times(1)).deleteByUuid(any());
    }
}
