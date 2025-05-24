package ru.aston.module5;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;
import ru.aston.module5.dto.UserEventDto;
import ru.aston.module5.service.EmailSender;

import java.time.Duration;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Testcontainers
class NotificationAppTests {

    private ListAppender<ILoggingEvent> listAppender;
    private Logger emailSenderLogger;

    private final UserEventDto userCreateDto = new UserEventDto("testName", "test_create@Email.com", UserEventDto.Event.CREATE);
    private final UserEventDto userDeleteDto = new UserEventDto("testName", "test_delete@Email.com", UserEventDto.Event.DELETE);

    @BeforeEach
    void setUp() {
        emailSenderLogger = (Logger) LoggerFactory.getLogger(EmailSender.class);

        listAppender = new ListAppender<>();
        listAppender.start();

        emailSenderLogger.addAppender(listAppender);
    }

    @AfterEach
    void tearDown() {
        emailSenderLogger.detachAppender(listAppender);
        listAppender.stop();
    }


    @Container
    private static final KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("apache/kafka-native:latest"));

    @DynamicPropertySource
    private static void setProperty(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
    }

    @Test
    public void consumerTest() {
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers());
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        KafkaProducer<String, UserEventDto> producer = new KafkaProducer<>(properties);
        producer.send(new ProducerRecord<>("userEventTopic", userCreateDto));
        producer.send(new ProducerRecord<>("userEventTopic", userDeleteDto));
        producer.close();

        await()
                .pollInterval(Duration.ofMillis(500))
                .atMost(30, TimeUnit.SECONDS)
                .untilAsserted(() -> {

                    List<ILoggingEvent> logsList = listAppender.list;

                    assertFalse(logsList.isEmpty());

                    assertThatLogContainsMessage(logsList, userCreateDto);
                    assertThatLogContainsMessage(logsList, userDeleteDto);
                });
    }

    private void assertThatLogContainsMessage(List<ILoggingEvent> logsList, UserEventDto dto) {
        assertTrue(logsList.stream()
                .map(ILoggingEvent::getFormattedMessage)
                .anyMatch(message -> message.contains("Сообщение о событии успешно отправлено на email: " + dto.email())));

    }
}
