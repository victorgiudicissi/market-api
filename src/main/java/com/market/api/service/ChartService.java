package com.market.api.service;

import com.market.api.dto.chart.ChartRequestDto;
import com.market.api.dto.chart.ChartResponseDto;

public interface ChartService {
    ChartResponseDto createChart(ChartRequestDto chartRequestDto);
}
