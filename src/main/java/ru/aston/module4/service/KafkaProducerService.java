package ru.aston.module4.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.aston.module4.dto.UserNotificationMessage;

@Slf4j
@Service
public class KafkaProducerService {
	private final KafkaTemplate<String, UserNotificationMessage> kafkaTemplate;

	public KafkaProducerService(KafkaTemplate<String, UserNotificationMessage> kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
	}

	public void sendMessage(String topic, UserNotificationMessage message) {
		kafkaTemplate.executeInTransaction(t -> t.send(topic, message))
				.whenComplete((result, e) -> {
					if (e != null) {
						log.error("Failed to send email notification to {}", message.getEmail(), e);
					} else {
						log.info("Email notification sent to {}", message.getEmail());
					}
				});
	}
}
