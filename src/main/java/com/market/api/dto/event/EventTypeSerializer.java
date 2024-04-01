package com.market.api.dto.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serializer;

public class EventTypeSerializer implements Serializer<EventType>{

    @Override
    public byte[] serialize(String topic, EventType eventType) {
        try {
            return new ObjectMapper().writeValueAsBytes(eventType);
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

}
