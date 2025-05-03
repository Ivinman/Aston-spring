package ru.aston.module4;

import org.junit.jupiter.api.Test;
import ru.aston.module4.dto.UserDto;
import ru.aston.module4.dto.UserModel;
import ru.aston.module4.mapper.UserMapper;
import ru.aston.module4.mapper.UserMapperImpl;
import ru.aston.module4.entity.User;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertAll;

public class MapperTest {
	private final UserMapper mapper = new UserMapperImpl();

	@Test
	void mapToDto_test() {
		User user = new User();
		user.setName("Ivan");
		user.setAge(44);
		user.setEmail("mail@mail.com");

		UserDto userDto = mapper.toDto(user);
		assertNotNull(userDto);
		assertEquals(user.getName(), userDto.getName());
		assertEquals(user.getAge(), userDto.getAge());
		assertEquals(user.getEmail(), userDto.getEmail());
	}

	@Test
	void mapToUser_test() {
		UserDto userDto = new UserDto("Ivan", 72, "mail@mail.com");

		User user = mapper.toUser(userDto);
		assertNotNull(user);
		assertEquals(userDto.getName(), user.getName());
		assertEquals(userDto.getAge(), user.getAge());
		assertEquals(userDto.getEmail(), user.getEmail());
	}

	@Test
	void updateUserFromDto_test() {
		User user = new User(42L, "Spy", 50, "mail@mail.com");

		UserModel userModel = new UserModel(74L, "Traitor", 30, "newmail@mail.com");

		mapper.updateUserFromDto(userModel, user);

		assertAll(
				() -> assertEquals(42L, user.getId()),
				() -> assertEquals("Traitor", user.getName()),
				() -> assertEquals(30, user.getAge()),
				() -> assertEquals("newmail@mail.com", user.getEmail())
		);
	}

	@Test
	void mapToDtoList_test() {
		List<User> userList = List.of(
				new User(42L, "Spy", 50, "mail@mail.com"),
				new User(74L, "Dog", 10, "mail@mail.ru")
		);

		List<UserDto> userDtoList = mapper.toDtoList(userList);

		assertThat(userDtoList)
				.usingRecursiveFieldByFieldElementComparator()
				.containsExactlyInAnyOrderElementsOf(
						userList.stream()
								.map(user -> new UserDto(user.getName(), user.getAge(), user.getEmail()))
								.toList()
				);
	}
}
