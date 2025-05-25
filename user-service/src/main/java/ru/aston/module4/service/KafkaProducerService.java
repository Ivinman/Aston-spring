package ru.aston.module4.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.aston.module4.dto.UserEventDto;
import ru.aston.module4.entity.KafkaEventEntity;
import ru.aston.module4.repository.KafkaEventRepository;

import java.time.LocalDateTime;

@Service
@Slf4j
public class KafkaProducerService {
	private final KafkaTemplate<String, UserEventDto> kafkaTemplate;
	private final KafkaEventRepository repository;
	private final String kafkaUserTopicName;
	private final String kafkaFallbackTopicName;

	public KafkaProducerService(
			KafkaTemplate<String, UserEventDto> kafkaTemplate,
			KafkaEventRepository repository,
			@Value("${user.event.topic.name}") String kafkaUserTopicName,
			@Value("${kafka.topic.fallback}") String kafkaFallbackTopicName
	) {
		this.kafkaTemplate = kafkaTemplate;
		this.repository = repository;
		this.kafkaUserTopicName = kafkaUserTopicName;
		this.kafkaFallbackTopicName = kafkaFallbackTopicName;
	}

	@CircuitBreaker(name = "kafkaCircuitBreaker", fallbackMethod = "userEventFallback")
	@Retry(name = "kafkaRetry")
	public void sendUserEvent(UserEventDto dto) {
		kafkaTemplate.send(kafkaUserTopicName, dto);
	}

	public void userEventFallback(UserEventDto dto, Throwable e) {
		log.error("Невозможно отправить событие {} в kafka: {}", dto, e.getMessage());

		repository.save(
				new KafkaEventEntity(
						kafkaFallbackTopicName,
						dto,
						LocalDateTime.now())
		);
	}
}
