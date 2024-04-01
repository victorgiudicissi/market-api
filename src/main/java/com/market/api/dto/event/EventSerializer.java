package com.market.api.dto.event;

import org.apache.kafka.common.serialization.Serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class EventSerializer implements Serializer<Event>{

    @Override
    public byte[] serialize(String topic, Event event) {
        try {
            return new ObjectMapper().writeValueAsBytes(event);
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

}
