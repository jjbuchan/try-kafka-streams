Trying out Kafka Streams by following the (outdated) article at https://www.baeldung.com/java-kafka-streams in addition to the official docs at https://kafka.apache.org/21/documentation/streams/tutorial.

## Running

```
# Start Kafka
docker-compose up -d

# Start consuming output topic
docker exec -it try-kafka-streams_kafka_1 \
       kafka-console-consumer --topic outputTopic --bootstrap-server localhost:9092 --from-beginning

# Run app
mvn spring-boot:run

# Send messages to Kafka
docker exec -it try-kafka-streams_kafka_1 \
       kafka-console-producer --topic inputTopic --broker-list localhost:9092

>this is a test
>blah blah blah

# See output in consumer
this has count of 1
is has count of 1
a has count of 1
test has count of 1
blah has count of 3

# Full consumer record also outputs to console
Got record: ConsumerRecord(topic = outputTopic, partition = 0,...
```