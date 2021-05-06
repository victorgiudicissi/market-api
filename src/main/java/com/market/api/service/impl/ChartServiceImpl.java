package com.market.api.service.impl;

import com.market.api.dto.chart.ChartRequestDto;
import com.market.api.dto.chart.ChartResponseDto;
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
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChartServiceImpl implements ChartService {

    private final ItemService itemService;
    private final ChartRepository chartRepository;

    @Override
    public ChartResponseDto createChart(ChartRequestDto chartRequestDto) {
        Chart chart = new Chart();

        AtomicReference<Long> amount = new AtomicReference<>(0L);

        Set<Item> chartItems = new HashSet<>();
        Set<Item> updatedItems = new HashSet<>();

        chartRequestDto.getItems()
        .forEach(itemFromRequest -> {
            ItemResponseDto itemFound = itemService.findItemByUuid(itemFromRequest.getUuid());

            if (Objects.isNull(itemFound)) {
                throw new DataNotFoundException("Não foi possível encontrar o item " + itemFromRequest.getUuid());
            }

            log.info("Item encontrado com sucesso. item: {}", itemFound);

            if (itemFound.getAmount().compareTo(itemFromRequest.getAmount()) < 0) {
                throw new OutOfStockException("A quantidade informada para o item " + itemFromRequest.getUuid() + " é maior do que a em estoque " + itemFound.getQuantity());
            }

            if (!itemFound.isEnabled()) {
                throw new UnavailableProductException("O produto " + itemFound.getDescription() + " não está ativo.");
            }

            ItemResponseDto chartItem = itemFound.toBuilder().quantity(itemFromRequest.getAmount()).build();
            ItemResponseDto updatedItem = itemFound.toBuilder().quantity(itemFound.getAmount() - itemFromRequest.getAmount()).build();

            amount.set(itemFound.getAmount() * itemFromRequest.getQuantity());

            updatedItems.add(updatedItem.toItem());
            chartItems.add(chartItem.toItem());
        });

        chart.setItems(chartItems);
        chart.setAmount(amount.get());
        chart.setStatus(Status.PENDING);
        chart.setCreatedAt(LocalDateTime.now());
        chart.setUpdatedAt(LocalDateTime.now());

        chartRepository.save(chart);
        itemService.saveAll(chartItems);

        return chart.toChartResponseDto();
    }
}
