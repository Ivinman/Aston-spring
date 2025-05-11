package ru.aston.module4.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.aston.module4.dto.UserEventDto;

@Service
public class KafkaProducerService {
	private final KafkaTemplate<String, UserEventDto> kafkaTemplate;
	@Value("${user.event.topic.name}")
	private final String kafkaUserTopicName;

	public KafkaProducerService(KafkaTemplate<String, UserEventDto> kafkaTemplate, String kafkaUserTopicName) {
		this.kafkaTemplate = kafkaTemplate;
		this.kafkaUserTopicName = kafkaUserTopicName;
	}

	public void sendUserEvent(UserEventDto dto) {
		kafkaTemplate.send(kafkaUserTopicName, dto);
	}
}
