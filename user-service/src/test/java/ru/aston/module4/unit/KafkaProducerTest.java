package ru.aston.module4.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import ru.aston.module4.dto.UserEventDto;
import ru.aston.module4.entity.KafkaEventEntity;
import ru.aston.module4.repository.KafkaEventRepository;
import ru.aston.module4.service.KafkaProducerService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class KafkaProducerTest {

	@Mock
	private KafkaTemplate<String, UserEventDto> kafkaTemplate;

	@Mock
	private KafkaEventRepository repository;

	@InjectMocks
	private KafkaProducerService service;

	private final UserEventDto dto = new UserEventDto(
			"Ivan",
			"test@mail.ru",
			UserEventDto.Event.CREATE
	);

	@BeforeEach
	void setUp() {
		service = new KafkaProducerService(
				kafkaTemplate,
				repository,
				"userEventTopic",
				"userFallbackTopic");
	}

	@Test
	void sendUserEvent_ok() {
		service.sendUserEvent(dto);
		verify(kafkaTemplate).send(anyString(), eq(dto));
	}

	@Captor
	private ArgumentCaptor<KafkaEventEntity> captor;

	@Test
	void userEventFallback_shouldSaveToFallbackQueue() {
		service.userEventFallback(dto, new RuntimeException("Kafka is down"));

		verify(repository).save(captor.capture());

		KafkaEventEntity event = captor.getValue();
		assertEquals(dto, event.getPayload());
		assertEquals("userFallbackTopic", event.getTopic());
		assertNotNull(event.getCreatedAt());
	}

}
