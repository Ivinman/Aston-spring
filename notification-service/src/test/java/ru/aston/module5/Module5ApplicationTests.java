package ru.aston.module5;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;
import ru.aston.module5.config.KafkaConfig;
import ru.aston.module5.dto.UserEventDto;
import ru.aston.module5.service.EmailSender;
import ru.aston.module5.service.UserEventKafkaListener;
import ru.aston.module5.util.MessageBuilder;

import java.time.Duration;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;


//@Import(TestcontainersConfiguration.class)
//@ContextConfiguration(classes = {Module5ApplicationTests.class, KafkaConfig.class, UserEventKafkaListener.class})
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class Module5ApplicationTests {
	//private final MessageBuilder messageBuilder = new MessageBuilder();
//	private final EmailSender emailSender = mock();
//	private final UserEventKafkaListener listener = new UserEventKafkaListener(emailSender);

	@MockitoBean
	private final EmailSender emailSender = mock();

	private final UserEventDto userCreateDto = new UserEventDto("testName", "test@Email.com", UserEventDto.Event.CREATE);

	private static final KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("apache/kafka-native:latest"));

	@BeforeAll
	static void set() {
		kafkaContainer.start();
		System.setProperty("spring.kafka.bootstrap-servers", kafkaContainer.getBootstrapServers());
	}

	@Test
	public void tess() {
		Properties properties = new Properties();
		properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers());
		properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

		KafkaProducer<String, UserEventDto> producer = new KafkaProducer<>(properties);
		producer.send(new ProducerRecord<>("userEventTopic", userCreateDto));
		producer.close();


//		verify(emailSender, times(1)).sendNotificationToEmail(any());

	}

//	@Test
//	void test() {
//		UserEventDto userCreateDto = new UserEventDto("testName", "test@Email.com", UserEventDto.Event.CREATE);
//		listener.listen(userCreateDto);
//		assertEquals("Здравствуйте! Аккаунт пользователя " + userCreateDto.username() + " успешно создан."
//				, messageBuilder.buildNotificationMessage(userCreateDto));
//		UserEventDto userDeleteDto = new UserEventDto("testName", "test@Email.com", UserEventDto.Event.DELETE);
//		listener.listen(userDeleteDto);
//		assertEquals("Здравствуйте! Аккаунт пользователя " + userDeleteDto.username() + " успешно удален."
//				, messageBuilder.buildNotificationMessage(userDeleteDto));
//	}

}
