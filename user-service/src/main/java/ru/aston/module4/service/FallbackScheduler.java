package ru.aston.module4.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.aston.module4.dto.UserEventDto;
import ru.aston.module4.entity.KafkaEventEntity;
import ru.aston.module4.repository.KafkaEventRepository;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class FallbackScheduler {

	private final KafkaTemplate<String, UserEventDto> kafkaTemplate;
	private final KafkaEventRepository repository;

	@Scheduled(fixedDelay = 10_000)
	public void retryFailedEvents() {
		List<KafkaEventEntity> events = repository.findAll();

		for (KafkaEventEntity event : events) {
			try {
				kafkaTemplate.send(event.getTopic(), event.getPayload());
				repository.delete(event);
				log.info("Событие {} повторно отправлено", event.getId());
			} catch (Exception e) {
				log.error("Ошибка при повторной отправке события: {}; {}", event.getId(), e.getMessage());
			}
		}
	}
}

