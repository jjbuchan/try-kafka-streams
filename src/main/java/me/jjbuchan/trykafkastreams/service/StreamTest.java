package me.jjbuchan.trykafkastreams.service;

import kafka.utils.TestUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Configuration
@EnableKafka
@EnableKafkaStreams
public class StreamTest {

    @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    public KafkaStreamsConfiguration kStreamsConfigs() {
        Map<String, Object> streamConfiguration = new HashMap<>();
        streamConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, "wordcount-stream-test");
        streamConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        streamConfiguration.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        streamConfiguration.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        streamConfiguration.put(StreamsConfig.STATE_DIR_CONFIG, TestUtils.tempDir().getAbsolutePath());
        return new KafkaStreamsConfiguration(streamConfiguration);
    }

    @Bean
    public KStream<String, String> kStream(StreamsBuilder builder) {
        String inputTopic = "inputTopic";
        String outputTopic = "outputTopic";
        Pattern pattern = Pattern.compile("\\W+", Pattern.UNICODE_CHARACTER_CLASS);

        KStream<String, String> stream = builder.stream(inputTopic, Consumed.with(Serdes.String(), Serdes.String()));
        stream
            .flatMapValues(value -> Arrays.asList(pattern.split(value.toLowerCase())))
            .groupBy((key, word) -> word)
            .count(Materialized.as("counts-store"))
            .toStream()
            .map((key, value) -> new KeyValue<>(key, key + " has count of " + value))
            .to(outputTopic, Produced.with(Serdes.String(), Serdes.String()));

        return stream;
    }

    @KafkaListener(topics = "outputTopic", groupId="streamTest")
    public void consumer(ConsumerRecord<String, String> message) {
        System.out.println("Got record: " + message.toString());
    }
}
