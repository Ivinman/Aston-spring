package ru.aston.module5.util;

import lombok.Getter;
import org.springframework.stereotype.Component;
import ru.aston.module5.dto.UserEventDto;

@Component
@Getter
public class MessageBuilder {
    private int messagesOnCreate;
    private int messagesOnDelete;

    public String buildNotificationMessage(UserEventDto dto) {
        UserEventDto.Event event = dto.event();
        if (event == UserEventDto.Event.CREATE) {
            messagesOnCreate++;
            return String.format("Здравствуйте! Аккаунт пользователя %s успешно создан." , dto.username());
        }

        if (event == UserEventDto.Event.DELETE) {
            messagesOnDelete++;
            return String.format("Здравствуйте! Аккаунт пользователя %s успешно удален." , dto.username());
        }

        return "";
    }
}
