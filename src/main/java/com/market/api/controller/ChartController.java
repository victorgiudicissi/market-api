package com.market.api.controller;

import com.market.api.dto.Page;
import com.market.api.dto.chart.ChartRequestDto;
import com.market.api.dto.chart.ChartResponseDto;
import com.market.api.dto.item.FilterChartRequestDto;
import com.market.api.model.enums.Status;
import com.market.api.service.ChartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/chart")
@RequiredArgsConstructor
public class ChartController {

    private final ChartService chartService;

    @PostMapping
    public ResponseEntity<ChartResponseDto> createChart(@Valid @RequestBody ChartRequestDto chartRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(chartService.createChart(chartRequestDto));
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<ChartResponseDto> getChartByUuid(@PathVariable("uuid") String uuid) {
        return ResponseEntity.status(HttpStatus.OK).body(chartService.getByUuid(uuid));
    }

    @GetMapping()
    public ResponseEntity<Page<ChartResponseDto>> filterChart(@Valid FilterChartRequestDto filter) {
        return ResponseEntity.status(HttpStatus.OK).body(chartService.findChartByFilter(filter));
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<ChartResponseDto> deleteChartByUuid(@PathVariable("uuid") String uuid) {
        chartService.deleteByUuid(uuid);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping("/{uuid}/status/{status}")
    public ResponseEntity<ChartResponseDto> changeChartStatus(@PathVariable("uuid") String uuid, @PathVariable("status") Status status) {
        return ResponseEntity.status(HttpStatus.OK).body(chartService.updateStatus(uuid, status));
    }
}
