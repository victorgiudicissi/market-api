package com.market.api.controller;

import com.market.api.dto.chart.ChartRequestDto;
import com.market.api.dto.chart.ChartResponseDto;
import com.market.api.service.ChartService;
import com.market.api.service.impl.ChartServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chart")
@RequiredArgsConstructor
public class ChartController {

    private ChartService chartService;

    @PostMapping
    public ResponseEntity<ChartResponseDto> createChart(@RequestBody ChartRequestDto chartRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(chartService.createChart(chartRequestDto));
    }
}
