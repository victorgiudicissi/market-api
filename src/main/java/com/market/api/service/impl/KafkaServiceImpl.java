package com.market.api.service.impl;

import com.market.api.dto.event.Event;
import com.market.api.dto.event.EventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaServiceImpl {
    @Value("${kafka.topic}")
    private String KAFKA_TOPIC;

    private final KafkaProducer<EventType, Event> producer;

    public void sendEvent(Event content, EventType eventType) {
        producer.send(new ProducerRecord<EventType, Event>(KAFKA_TOPIC, eventType, content));

        log.info("Event sent successfully. data: {}", content);
    }
}
