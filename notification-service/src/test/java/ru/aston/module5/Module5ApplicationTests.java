package ru.aston.module5;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
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
import ru.aston.module5.util.MessageBuilder;

import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Testcontainers
class Module5ApplicationTests {
	private final MessageBuilder messageBuilder;

	private final UserEventDto userCreateDto = new UserEventDto("testName", "test@Email.com", UserEventDto.Event.CREATE);
	private final UserEventDto userDeleteDto = new UserEventDto("testName", "test@Email.com", UserEventDto.Event.DELETE);

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
				.pollInterval(Duration.ofSeconds(3))
				.atMost(5, TimeUnit.SECONDS)
				.untilAsserted(() -> {
					Assertions.assertEquals(1, messageBuilder.getMessagesOnCreate());
					Assertions.assertEquals(1, messageBuilder.getMessagesOnDelete());
				});
	}

	@Test
	void testEmailSender() {
		Assertions.assertEquals("Здравствуйте! Аккаунт пользователя " + userCreateDto.username() + " успешно создан.",
				messageBuilder.buildNotificationMessage(userCreateDto));
		Assertions.assertEquals("Здравствуйте! Аккаунт пользователя " + userDeleteDto.username() + " успешно удален.",
				messageBuilder.buildNotificationMessage(userDeleteDto));
	}

}
