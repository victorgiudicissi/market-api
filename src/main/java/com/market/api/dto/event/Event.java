package com.market.api.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Event<T> {
    private EventType type;
    private EventAction action;
    private LocalDateTime createdAt;
    private T content;
}
