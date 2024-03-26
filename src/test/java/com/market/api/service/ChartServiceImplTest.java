package com.market.api.service;

import com.market.api.dto.Page;
import com.market.api.dto.chart.ChartRequestDto;
import com.market.api.dto.chart.ChartResponseDto;
import com.market.api.dto.item.ChartItemRequestDto;
import com.market.api.dto.item.FilterChartRequestDto;
import com.market.api.dto.item.ItemResponseDto;
import com.market.api.exception.DataNotFoundException;
import com.market.api.exception.OutOfStockException;
import com.market.api.exception.UnavailableProductException;
import com.market.api.model.Chart;
import com.market.api.model.Item;
import com.market.api.model.enums.Status;
import com.market.api.repository.ChartRepository;
import com.market.api.service.impl.ChartServiceImpl;
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

class ChartServiceImplTest {

    @Mock
    private ItemService itemService;

    @Mock
    private ChartRepository chartRepository;

    @Mock
    private MongoTemplate mongoTemplate;

    @InjectMocks
    private ChartServiceImpl chartService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createChart_WhenItemsAreAvailableAndInStock_ShouldReturnChartResponseDto() {
        // GIVEN
        ChartRequestDto chartRequestDto = ChartRequestDto
                .builder()
                .marketUuid("mock-market-uuid")
                .items(List.of(ChartItemRequestDto.builder()
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
        when(chartRepository.save(any())).thenReturn(mock(Chart.class));

        // WHEN
        ChartResponseDto result = chartService.createChart(chartRequestDto);

        // THEN
        assertNotNull(result);
        assertEquals("mock-market-uuid", result.getMarketUuid());
        assertEquals(500L, result.getPrice());
        assertEquals(1, result.getItems().size());
    }

    @Test
    void createChart_WhenItemsAreNotInStock_ShouldThrowOutOfStockException() {
        // GIVEN
        ChartRequestDto chartRequestDto = ChartRequestDto
                .builder()
                .marketUuid("mock-market-uuid")
                .items(List.of(ChartItemRequestDto.builder()
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
        when(chartRepository.save(any())).thenReturn(mock(Chart.class));


        // WHEN - THEN
        assertThrows(OutOfStockException.class, () -> chartService.createChart(chartRequestDto));
        verify(itemService, times(1)).findItemByUuid("first-item-uuid");
        verify(chartRepository, times(0)).save(any());
    }

    @Test
    void createChart_WhenItemsAreDisabled_ShouldThrowUnavailableProductException() {
        // GIVEN
        ChartRequestDto chartRequestDto = ChartRequestDto
                .builder()
                .marketUuid("mock-market-uuid")
                .items(List.of(ChartItemRequestDto.builder()
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
        when(chartRepository.save(any())).thenReturn(mock(Chart.class));


        // WHEN - THEN
        assertThrows(UnavailableProductException.class, () -> chartService.createChart(chartRequestDto));
        verify(itemService, times(1)).findItemByUuid("first-item-uuid");
        verify(chartRepository, times(0)).save(any());
    }

    @Test
    void getByUuid_WhenItemIsFound_ShouldReturnItem() {
        // GIVEN
        Chart chart = Chart
                .builder()
                .marketUuid("mock-market-uuid")
                .items(Set.of(Item.builder()
                        .uuid("first-item-uuid")
                        .quantity(10L)
                        .build())
                )
                .price(100L)
                .build();

        when(chartRepository.findById(anyString())).thenReturn(ofNullable(chart));

        // WHEN
        ChartResponseDto result = chartService.getByUuid("mock-chart-uuid");

        // THEN
        assertEquals(100L, result.getPrice());
    }

    @Test
    void getByUuid_WhenItemIsNotFound_ShouldThrowDataNotFoundException() {
        // GIVEN
        when(chartRepository.findById(anyString())).thenReturn(Optional.empty());

        // WHEN
        assertThrows(DataNotFoundException.class, () -> chartService.getByUuid("mock-chart-uuid"));
    }

    @Test
    void updateStatus_WhenItemIsFound_ShouldReturnItem() {
        // GIVEN
        Chart chart = Chart
                .builder()
                .marketUuid("mock-market-uuid")
                .items(Set.of(Item.builder()
                        .uuid("first-item-uuid")
                        .quantity(10L)
                        .build())
                )
                .price(100L)
                .build();

        when(chartRepository.findById(anyString())).thenReturn(ofNullable(chart));
        when(chartRepository.save(any())).thenReturn(chart);

        // WHEN
        ChartResponseDto result = chartService.updateStatus("mock-chart-uuid", Status.PAYED);

        // THEN
        assertEquals(100L, result.getPrice());
    }

    @Test
    void deleteByUuid_WhenItemIsFound_ShouldReturnItem() {
        // GIVEN
        Chart chart = Chart
                .builder()
                .marketUuid("mock-market-uuid")
                .items(Set.of(Item.builder()
                        .uuid("first-item-uuid")
                        .quantity(10L)
                        .build())
                )
                .price(100L)
                .build();

        when(chartRepository.findById(anyString())).thenReturn(ofNullable(chart));
        doNothing().when(chartRepository).deleteById(any());

        // WHEN
        chartService.deleteByUuid("mock-chart-uuid");

        // THEN
        verify(chartRepository, times(1)).deleteById(anyString());
    }

    @Test
    void findChartByFilter_WhenItemIsFound_ShouldReturnItem() {
        // GIVEN
        FilterChartRequestDto filter = FilterChartRequestDto.builder()
                .marketUuid("market-uuid")
                .status(Status.PAYED)
                .page(1)
                .size(10)
                .build();

        Chart chart = Chart
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
        when(mongoTemplate.find(any(), any())).thenReturn(List.of(chart));

        // WHEN
        Page<ChartResponseDto> result = chartService.findChartByFilter(filter);

        // THEN
        assertEquals(1, result.getPage());
        assertEquals(10, result.getSize());
        assertEquals(1, result.getContent().size());
    }
}
