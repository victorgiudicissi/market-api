package com.market.api.config;

import com.market.api.dto.event.Event;
import com.market.api.dto.event.EventSerializer;
import com.market.api.dto.event.EventType;
import com.market.api.dto.event.EventTypeSerializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

import static org.apache.kafka.clients.producer.ProducerConfig.*;

@Slf4j
@Configuration
public class KafkaConfig {

    @Value("${kafka.host}")
    private String KAFKA_HOST;

    @Value("${kafka.port}")
    private String KAFKA_PORT;

    @Bean
    public KafkaProducer<EventType, Event> kafkaProducer() {
        Properties properties = new Properties();

        String serverConfig = String.format("%s:%s", KAFKA_HOST, KAFKA_PORT);
        log.info("Creating kafka producer bean. host: {}", serverConfig);

        properties.put(BOOTSTRAP_SERVERS_CONFIG, serverConfig);
        properties.put(KEY_SERIALIZER_CLASS_CONFIG, EventTypeSerializer.class.getName());
        properties.put(VALUE_SERIALIZER_CLASS_CONFIG, EventSerializer.class.getName());

        return new KafkaProducer<>(properties);
    }
}
