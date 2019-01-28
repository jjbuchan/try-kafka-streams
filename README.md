Trying out Kafka Streams by following the (outdated) article at https://www.baeldung.com/java-kafka-streams in addition to the official docs at https://kafka.apache.org/21/documentation/streams/tutorial.

## Running

```
# Start Kafka
docker-compose up -d

# Run app
mvn spring-boot:run

# Send messages to Kafka
docker exec -it try-kafka-streams_kafka_1 \
       kafka-console-producer --topic inputTopic --broker-list localhost:9092

>this is a test
>blah blah blah

# See output in console
word: this -> 1
word: is -> 1
word: a -> 1
word: test -> 1
word: blah -> 3


# View topic contents manually
docker exec -it try-kafka-streams_kafka_1 \
       kafka-console-consumer --topic inputTopic --bootstrap-server localhost:9092 --from-beginning 