package ru.aston.module5.service;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.aston.module5.dto.UserEventDto;

@RequiredArgsConstructor
@Component
public class UserEventKafkaListener {
    private final EmailSender emailSender;

    @KafkaListener(topics = "${user.event.topic.name}",
                    groupId = "${spring.kafka.consumer.group-id}")
    public void listen(UserEventDto dto) {
        emailSender.sendNotificationToEmail(dto);
    }
}
