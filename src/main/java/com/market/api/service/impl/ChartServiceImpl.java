package com.market.api.service.impl;

import com.market.api.dto.Page;
import com.market.api.dto.chart.ChartRequestDto;
import com.market.api.dto.chart.ChartResponseDto;
import com.market.api.dto.item.FilterChartRequestDto;
import com.market.api.dto.item.FilterItemRequestDto;
import com.market.api.dto.item.ItemResponseDto;
import com.market.api.exception.DataNotFoundException;
import com.market.api.exception.OutOfStockException;
import com.market.api.exception.UnavailableProductException;
import com.market.api.model.Chart;
import com.market.api.model.Item;
import com.market.api.model.enums.Status;
import com.market.api.repository.ChartRepository;
import com.market.api.service.ChartService;
import com.market.api.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChartServiceImpl implements ChartService {

    private final ItemService itemService;
    private final ChartRepository chartRepository;
    private final MongoTemplate mongoTemplate;

    @Override
    public ChartResponseDto createChart(ChartRequestDto chartRequestDto) {
        Chart chart = new Chart();

        AtomicReference<Long> chartPrice = new AtomicReference<>(0L);

        Set<Item> chartItems = new HashSet<>();
        Set<Item> updatedItems = new HashSet<>();

        chartRequestDto.getItems()
        .forEach(itemFromRequest -> {
            ItemResponseDto itemFound = itemService.findItemByUuid(itemFromRequest.getUuid());

            log.info(String.format("Item found. item: %s", itemFound));

            if (itemFound.getQuantity().compareTo(itemFromRequest.getQuantity()) < 0) {
                throw new OutOfStockException(String.format("The quantity informed for item %s is greater than the stock %s.", itemFromRequest.getUuid(), itemFound.getQuantity()));
            }

            if (!itemFound.isEnabled()) {
                throw new UnavailableProductException(String.format("The product %s is disabled.", itemFound.getDescription()));
            }

            ItemResponseDto chartItem = itemFound.toBuilder().quantity(itemFromRequest.getQuantity()).build();

            ItemResponseDto updatedItem = itemFound.toBuilder().quantity(itemFound.getQuantity() - itemFromRequest.getQuantity()).build();

            chartPrice.set(itemFound.getPrice() * itemFromRequest.getQuantity());

            updatedItems.add(updatedItem.toItem());
            chartItems.add(chartItem.toItem());
        });

        chart.setUuid(UUID.randomUUID().toString());
        chart.setMarketUuid(chartRequestDto.getMarketUuid());
        chart.setItems(chartItems);
        chart.setPrice(chartPrice.get());
        chart.setStatus(Status.PENDING);
        chart.setCreatedAt(LocalDateTime.now());
        chart.setUpdatedAt(LocalDateTime.now());

        chartRepository.save(chart);
        itemService.saveAll(updatedItems);

        return chart.toChartResponseDto();
    }

    public ChartResponseDto getByUuid(String uuid) {
        return this.findById(uuid).toChartResponseDto();
    }

    @Override
    public Page<ChartResponseDto> findChartByFilter(FilterChartRequestDto filter) {
        log.info("Filtering charts. filter: {}", filter);
        PageRequest pageRequest = PageRequest.of(filter.getPage() - 1, filter.getSize());

        Query query = new Query();

        if (filter.getMarketUuid() != null) {
            query.addCriteria(Criteria.where("marketUuid").regex(filter.getMarketUuid(), "i"));
        } else if (filter.getStatus() != null) {
            query.addCriteria(Criteria.where("status").regex(filter.getStatus().toString(), "i"));
        }

        long totalCount = mongoTemplate.count(query, Chart.class);

        query.with(pageRequest);

        List<Chart> chart = mongoTemplate.find(query, Chart.class);

        List<ChartResponseDto> itemsDTO = chart.stream()
                .map(Chart::toChartResponseDto)
                .collect(Collectors.toList());

        Page<ChartResponseDto> pagination = new Page<>();
        pagination.setPage(filter.getPage());
        pagination.setCount(totalCount);
        pagination.setSize(filter.getSize());
        pagination.setContent(itemsDTO);

        return pagination;
    }

    public void deleteByUuid(String uuid) {
        this.findById(uuid);
        chartRepository.deleteById(uuid);
    }

    @Override
    public ChartResponseDto updateStatus(String uuid, Status status) {
        Chart chart = findById(uuid);

        chart.setStatus(status);
        return chartRepository.save(chart).toChartResponseDto();
    }

    private Chart findById(String uuid) {
        Optional<Chart> optChart = chartRepository.findById(uuid);

        if (optChart.isEmpty()) {
            String message = "Chart with uuid %s not found.";
            log.error(String.format(message, uuid));
            throw new DataNotFoundException(String.format(message, uuid));
        }

        return optChart.get();
    }
}
