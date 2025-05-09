package ru.aston.module5.util;

import org.springframework.stereotype.Component;
import ru.aston.module5.dto.UserEventDto;

@Component
public class MessageBuilder {

    public String buildNotificationMessage(UserEventDto dto) {
        UserEventDto.Event event = dto.event();
        if (event == UserEventDto.Event.CREATE) {
            return String.format("Здравствуйте! Аккаунт пользователя %s успешно создан." , dto.username());
        }

        if (event == UserEventDto.Event.DELETE) {
            return String.format("Здравствуйте! Аккаунт пользователя %s успешно удален." , dto.username());
        }

        return "";
    }
}
