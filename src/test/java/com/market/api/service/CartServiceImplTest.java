package com.market.api.service;

import com.market.api.dto.Page;
import com.market.api.dto.cart.CartRequestDto;
import com.market.api.dto.cart.CartResponseDto;
import com.market.api.dto.item.CartItemRequestDto;
import com.market.api.dto.item.FilterCartRequestDto;
import com.market.api.dto.item.ItemResponseDto;
import com.market.api.exception.DataNotFoundException;
import com.market.api.exception.OutOfStockException;
import com.market.api.exception.UnavailableProductException;
import com.market.api.model.Cart;
import com.market.api.model.Item;
import com.market.api.model.enums.Status;
import com.market.api.repository.CartRepository;
import com.market.api.service.impl.CartServiceImpl;
import com.market.api.service.impl.KafkaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.util.Optional.ofNullable;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CartServiceImplTest {

    @Mock
    private ItemService itemService;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private MongoTemplate mongoTemplate;

    @Mock
    private KafkaServiceImpl kafkaService;

    @InjectMocks
    private CartServiceImpl cartService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createCart_WhenItemsAreAvailableAndInStock_ShouldReturnCartResponseDto() {
        // GIVEN
        CartRequestDto cartRequestDto = CartRequestDto
                .builder()
                .marketUuid("mock-market-uuid")
                .items(List.of(CartItemRequestDto.builder()
                        .uuid("first-item-uuid")
                        .quantity(10L)
                        .build())
                )
                .build();

        when(itemService.findItemByUuid(any())).thenReturn(ItemResponseDto.builder()
                        .price(50L)
                        .quantity(100L)
                        .enabled(true)
                .build());
        when(cartRepository.save(any())).thenReturn(mock(Cart.class));
        doNothing().when(kafkaService).sendEvent(any(), any());

        // WHEN
        CartResponseDto result = cartService.createCart(cartRequestDto);

        // THEN
        assertNotNull(result);
        assertEquals("mock-market-uuid", result.getMarketUuid());
        assertEquals(500L, result.getPrice());
        assertEquals(1, result.getItems().size());
    }

    @Test
    void createCart_WhenItemsAreNotInStock_ShouldThrowOutOfStockException() {
        // GIVEN
        CartRequestDto cartRequestDto = CartRequestDto
                .builder()
                .marketUuid("mock-market-uuid")
                .items(List.of(CartItemRequestDto.builder()
                        .uuid("first-item-uuid")
                        .quantity(11L)
                        .build())
                )
                .build();

        when(itemService.findItemByUuid(any())).thenReturn(ItemResponseDto.builder()
                .price(50L)
                .quantity(10L)
                .enabled(true)
                .build());
        when(cartRepository.save(any())).thenReturn(mock(Cart.class));
        doNothing().when(kafkaService).sendEvent(any(), any());


        // WHEN - THEN
        assertThrows(OutOfStockException.class, () -> cartService.createCart(cartRequestDto));
        verify(itemService, times(1)).findItemByUuid("first-item-uuid");
        verify(cartRepository, times(0)).save(any());
    }

    @Test
    void createCart_WhenItemsAreDisabled_ShouldThrowUnavailableProductException() {
        // GIVEN
        CartRequestDto cartRequestDto = CartRequestDto
                .builder()
                .marketUuid("mock-market-uuid")
                .items(List.of(CartItemRequestDto.builder()
                        .uuid("first-item-uuid")
                        .quantity(10L)
                        .build())
                )
                .build();

        when(itemService.findItemByUuid(any())).thenReturn(ItemResponseDto.builder()
                .price(50L)
                .quantity(10L)
                .enabled(false)
                .build());
        when(cartRepository.save(any())).thenReturn(mock(Cart.class));
        doNothing().when(kafkaService).sendEvent(any(), any());


        // WHEN - THEN
        assertThrows(UnavailableProductException.class, () -> cartService.createCart(cartRequestDto));
        verify(itemService, times(1)).findItemByUuid("first-item-uuid");
        verify(cartRepository, times(0)).save(any());
    }

    @Test
    void getByUuid_WhenItemIsFound_ShouldReturnItem() {
        // GIVEN
        Cart cart = Cart
                .builder()
                .marketUuid("mock-market-uuid")
                .items(Set.of(Item.builder()
                        .uuid("first-item-uuid")
                        .quantity(10L)
                        .build())
                )
                .price(100L)
                .build();

        when(cartRepository.findById(anyString())).thenReturn(ofNullable(cart));

        // WHEN
        CartResponseDto result = cartService.getByUuid("mock-cart-uuid");

        // THEN
        assertEquals(100L, result.getPrice());
    }

    @Test
    void getByUuid_WhenItemIsNotFound_ShouldThrowDataNotFoundException() {
        // GIVEN
        when(cartRepository.findById(anyString())).thenReturn(Optional.empty());

        // WHEN
        assertThrows(DataNotFoundException.class, () -> cartService.getByUuid("mock-cart-uuid"));
    }

    @Test
    void updateStatus_WhenItemIsFound_ShouldReturnItem() {
        // GIVEN
        Cart cart = Cart
                .builder()
                .marketUuid("mock-market-uuid")
                .items(Set.of(Item.builder()
                        .uuid("first-item-uuid")
                        .quantity(10L)
                        .build())
                )
                .price(100L)
                .build();

        when(cartRepository.findById(anyString())).thenReturn(ofNullable(cart));
        when(cartRepository.save(any())).thenReturn(cart);
        doNothing().when(kafkaService).sendEvent(any(), any());

        // WHEN
        CartResponseDto result = cartService.updateStatus("mock-cart-uuid", Status.PAYED);

        // THEN
        assertEquals(100L, result.getPrice());
    }

    @Test
    void deleteByUuid_WhenItemIsFound_ShouldReturnItem() {
        // GIVEN
        Cart cart = Cart
                .builder()
                .marketUuid("mock-market-uuid")
                .items(Set.of(Item.builder()
                        .uuid("first-item-uuid")
                        .quantity(10L)
                        .build())
                )
                .price(100L)
                .build();

        when(cartRepository.findById(anyString())).thenReturn(ofNullable(cart));
        doNothing().when(cartRepository).deleteById(any());
        doNothing().when(kafkaService).sendEvent(any(), any());

        // WHEN
        cartService.deleteByUuid("mock-cart-uuid");

        // THEN
        verify(cartRepository, times(1)).deleteById(anyString());
    }

    @Test
    void findCartByFilter_WhenItemIsFound_ShouldReturnItem() {
        // GIVEN
        FilterCartRequestDto filter = FilterCartRequestDto.builder()
                .marketUuid("market-uuid")
                .status(Status.PAYED)
                .page(1)
                .size(10)
                .build();

        Cart cart = Cart
                .builder()
                .marketUuid("mock-market-uuid")
                .items(Set.of(Item.builder()
                        .uuid("first-item-uuid")
                        .quantity(10L)
                        .build())
                )
                .price(100L)
                .build();

        when(mongoTemplate.count(any(), anyString())).thenReturn(1L);
        when(mongoTemplate.find(any(), any())).thenReturn(List.of(cart));

        // WHEN
        Page<CartResponseDto> result = cartService.findCartByFilter(filter);

        // THEN
        assertEquals(1, result.getPage());
        assertEquals(10, result.getSize());
        assertEquals(1, result.getContent().size());
    }
}
