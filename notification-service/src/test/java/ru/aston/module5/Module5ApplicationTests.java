package ru.aston.module5;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.aston.module5.dto.UserEventDto;
import ru.aston.module5.service.UserEventKafkaListener;
import ru.aston.module5.util.MessageBuilder;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class Module5ApplicationTests {
	private final MessageBuilder messageBuilder;
	private final UserEventKafkaListener listener;

	@Test
	void testEmailSender() {
		UserEventDto userCreateDto = new UserEventDto("testName", "test@Email.com", UserEventDto.Event.CREATE);
		listener.listen(userCreateDto);
		Assertions.assertEquals("Здравствуйте! Аккаунт пользователя " + userCreateDto.username() + " успешно создан.",
				messageBuilder.buildNotificationMessage(userCreateDto));

		UserEventDto userDeleteDto = new UserEventDto("testName", "test@Email.com", UserEventDto.Event.DELETE);
		listener.listen(userDeleteDto);
		Assertions.assertEquals("Здравствуйте! Аккаунт пользователя " + userDeleteDto.username() + " успешно удален.",
				messageBuilder.buildNotificationMessage(userDeleteDto));
	}

}
