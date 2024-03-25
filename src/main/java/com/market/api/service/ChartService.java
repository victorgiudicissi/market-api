package com.market.api.service;

import com.market.api.dto.Page;
import com.market.api.dto.chart.ChartRequestDto;
import com.market.api.dto.chart.ChartResponseDto;
import com.market.api.dto.item.FilterChartRequestDto;
import com.market.api.model.enums.Status;

public interface ChartService {
    ChartResponseDto createChart(ChartRequestDto chartRequestDto);
    ChartResponseDto getByUuid(String uuid);
    Page<ChartResponseDto> findChartByFilter(FilterChartRequestDto filter);
    void deleteByUuid(String uuid);
    ChartResponseDto updateStatus(String uuid, Status status);
}
