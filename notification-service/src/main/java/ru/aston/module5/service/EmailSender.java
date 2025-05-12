package ru.aston.module5.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.aston.module5.dto.UserEventDto;
import ru.aston.module5.util.MessageBuilder;

@Slf4j
@RequiredArgsConstructor
@Service
public class EmailSender {
    private final MessageBuilder messageBuilder;

    public void sendNotificationToEmail(UserEventDto dto) {
        String message = messageBuilder.buildNotificationMessage(dto);
        if (!message.isEmpty()) {
            System.out.println(message);
            System.out.printf("Сообщение о событии успешно отправлено на email: %s%n", dto.email());
        }
    }
}
