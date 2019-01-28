package me.jjbuchan.trykafkastreams.service;

import kafka.utils.TestUtils;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Properties;
import java.util.regex.Pattern;

@Service
public class StreamTest {

    public StreamTest() {
        String inputTopic = "inputTopic";
        Properties streamConfiguration = new Properties();
        streamConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, "wordcount-stream-test");
        streamConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        streamConfiguration.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        streamConfiguration.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        streamConfiguration.put(StreamsConfig.STATE_DIR_CONFIG, TestUtils.tempDir().getAbsolutePath());

        final StreamsBuilder builder = new StreamsBuilder();
        KStream<String, String> text = builder.stream(inputTopic);
        Pattern pattern = Pattern.compile("\\W+", Pattern.UNICODE_CHARACTER_CLASS);

        KTable<String, Long> wordCounts = text
                .flatMapValues(value -> Arrays.asList(pattern.split(value.toLowerCase())))
                .groupBy((key, word) -> word)
                .count();

        wordCounts.toStream().foreach((w, c) -> System.out.println("word: " + w + " -> " + c));

        KafkaStreams stream = new KafkaStreams(builder.build(), streamConfiguration);
        stream.start();
    }

}
