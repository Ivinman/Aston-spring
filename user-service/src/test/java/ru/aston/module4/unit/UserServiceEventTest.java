package ru.aston.module4.unit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.aston.module4.dto.UserDto;
import ru.aston.module4.dto.UserEventDto;
import ru.aston.module4.entity.User;
import ru.aston.module4.mapper.UserMapper;
import ru.aston.module4.repository.UserRepository;
import ru.aston.module4.service.KafkaProducerService;
import ru.aston.module4.service.UserServiceImpl;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceEventTest {
	@Mock
	private UserRepository repository;

	@Mock
	private UserMapper mapper;

	@Mock
	private KafkaProducerService kafkaProducer;

	@InjectMocks
	private UserServiceImpl service;

	private final UserDto userDto = UserDto.builder()
			.name("test")
			.age(23)
			.email("test@email.ru")
			.build();

	private static final User user = new User();

	@BeforeAll
	public static void createUser() {
		user.setName("test");
		user.setAge(23);
		user.setEmail("test@email.ru");
	}

	@Test
	public void addUser_sendEvent() {
		User savedUser = new User(1L, "test", 23, "test@email.ru");

		when(mapper.toUser(userDto)).thenReturn(user);
		when(repository.save(any(User.class))).thenReturn(savedUser);

		service.createUser(userDto);

		UserEventDto expectedEvent = new UserEventDto(
				"test",
				"test@email.ru",
				UserEventDto.Event.CREATE
		);
		verify(kafkaProducer).sendUserEvent(expectedEvent);
	}

	@Test
	public void deleteUser_sendEvent() {
		Long userId = 1L;

		when(repository.findById(userId)).thenReturn(Optional.of(user));

		service.deleteUser(1L);

		UserEventDto expectedEvent = new UserEventDto(
				"test",
				"test@email.ru",
				UserEventDto.Event.DELETE
		);
		verify(kafkaProducer).sendUserEvent(expectedEvent);
	}
}
