package com.market.api.dto.exception;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ExceptionResponseDto {
    @JsonProperty("error")
    private final String errorCode;

    @JsonProperty("error_description")
    private final String errorDescription;
}
