package ru.aston.module4.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.aston.module4.dto.UserEventDto;

@Service
public class KafkaProducerService {
	private final KafkaTemplate<String, UserEventDto> kafkaTemplate;

	public KafkaProducerService(KafkaTemplate<String, UserEventDto> kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
	}

	public void sendMessage(String topic, UserEventDto dto) {
		kafkaTemplate.send(topic, dto);
	}
}
