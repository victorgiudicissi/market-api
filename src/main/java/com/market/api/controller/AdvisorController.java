package com.market.api.controller;

import com.market.api.dto.exception.ExceptionResponseDto;
import com.market.api.exception.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@Order(-1)
@RequiredArgsConstructor
public class AdvisorController {
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DataNotFoundException.class)
    public ExceptionResponseDto ameExceptionHandler(DataNotFoundException ex) {
        return new ExceptionResponseDto(ex.getClass().getSimpleName(), ex.getMessage());
    }
}
