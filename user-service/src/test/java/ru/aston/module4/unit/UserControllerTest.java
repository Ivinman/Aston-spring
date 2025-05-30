package ru.aston.module4.unit;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import ru.aston.module4.UserServiceApp;
import ru.aston.module4.dto.UserDto;
import ru.aston.module4.dto.UserUpdateDto;
import ru.aston.module4.exception.AlreadyExistException;
import ru.aston.module4.exception.NotFoundException;
import ru.aston.module4.mapper.RequestType;
import ru.aston.module4.mapper.UserModelAssembler;
import ru.aston.module4.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest
@ContextConfiguration(classes = UserServiceApp.class)
class UserControllerTest {
	private final ObjectMapper objectMapper = new ObjectMapper();

	@MockitoBean
	private final UserService service = mock();

	@MockitoBean
	private final UserModelAssembler assembler = mock();

	private MockMvc mvc;

	private final UserDto userDto = UserDto.builder()
			.name("test")
			.age(23)
			.email("test@email.ru")
			.build();

	private final UserDto badUserDto = UserDto.builder()
			.name("t")
			.age(-4)
			.email("t st@")
			.build();

	private final UserUpdateDto userUpdateDto = new UserUpdateDto(2L, "testSecond", 23, "testSecond@email.ru");

	@BeforeEach
	void setUp(WebApplicationContext wac) {
		mvc = MockMvcBuilders
				.webAppContextSetup(wac)
				.build();
	}

	@SneakyThrows
	@Test
	public void addUser() {
		when(service.createUser(any())).thenReturn(userDto).thenThrow(AlreadyExistException.class);
		when(assembler.toModel(userDto, RequestType.CREATE))
				.thenReturn(EntityModel.of(userDto)
						.add(Link.of("http://localhost/users/1", "self"))
						.add(Link.of("http://localhost/users/1", "update"))
						.add(Link.of("http://localhost/users/1", "delete")));

		mvc.perform(post("/users")
						.content(objectMapper.writeValueAsString(userDto))
						.characterEncoding(StandardCharsets.UTF_8)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))

				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.name", is(userDto.getName())))
				.andExpect(jsonPath("$.email", is(userDto.getEmail())))
				.andExpect(jsonPath("$.age", is(userDto.getAge())))
				.andExpect(jsonPath("$._links.self.href").exists())
				.andExpect(jsonPath("$._links.update.href").exists())
				.andExpect(jsonPath("$._links.delete.href").exists());

		mvc.perform(post("/users")
						.content(objectMapper.writeValueAsString(badUserDto))
						.characterEncoding(StandardCharsets.UTF_8)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))

				.andExpect(status().isBadRequest());

		mvc.perform(post("/users")
						.content(objectMapper.writeValueAsString(userDto))
						.characterEncoding(StandardCharsets.UTF_8)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))

				.andExpect(status().isConflict());
	}

	@SneakyThrows
	@Test
	public void updateUser() {
		UserDto updatedUserDto = UserDto.builder()
				.name(userUpdateDto.getName())
				.age(userUpdateDto.getAge())
				.email(userUpdateDto.getEmail())
				.build();

		when(service.updateUser(any(), any())).thenReturn(updatedUserDto)
				.thenThrow(NotFoundException.class)
				.thenThrow(AlreadyExistException.class);
		when(assembler.toModel(updatedUserDto, RequestType.UPDATE))
				.thenReturn(EntityModel.of(updatedUserDto)
						.add(Link.of("http://localhost/users/1", "self"))
						.add(Link.of("http://localhost/users/1", "delete")));

		mvc.perform(patch("/users/{userId}", 1L)
						.content(objectMapper.writeValueAsString(userUpdateDto))
						.characterEncoding(StandardCharsets.UTF_8)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))

				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name", is(userUpdateDto.getName())))
				.andExpect(jsonPath("$.age", is(userUpdateDto.getAge())))
				.andExpect(jsonPath("$.email", is(userUpdateDto.getEmail())))
				.andExpect(jsonPath("$._links.self.href").exists())
				.andExpect(jsonPath("$._links.delete.href").exists());

		mvc.perform(patch("/users/{userId}", 1)
						.content(objectMapper.writeValueAsString(badUserDto))
						.characterEncoding(StandardCharsets.UTF_8)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());

		mvc.perform(patch("/users/{userId}", 23)
						.content(objectMapper.writeValueAsString(userUpdateDto))
						.characterEncoding(StandardCharsets.UTF_8)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());

		mvc.perform(patch("/users/{userId}", 1)
						.content(objectMapper.writeValueAsString(userUpdateDto))
						.characterEncoding(StandardCharsets.UTF_8)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isConflict());
	}

	@SneakyThrows
	@Test
	public void deleteUser() {
		mvc.perform(delete("/users/{userId}", 1L)
						.characterEncoding(StandardCharsets.UTF_8)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNoContent());

		verify(service, times(1)).deleteUser(any());
		doThrow(new NotFoundException("not found")).when(service).deleteUser(any());

		mvc.perform(delete("/users/{userId}", 1L)
						.characterEncoding(StandardCharsets.UTF_8)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}

	@SneakyThrows
	@Test
	public void findAllUsers() {
		List<UserDto> userDtos = List.of(userDto, badUserDto);
		when(service.findAllUsers()).thenReturn(userDtos);
		when(assembler.toModel(userDto, RequestType.LIST))
				.thenReturn(EntityModel.of(userDto));
		when(assembler.toModel(badUserDto, RequestType.LIST))
				.thenReturn(EntityModel.of(badUserDto));

		mvc.perform(get("/users"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$._embedded.userDtoList").isArray())
				.andExpect(jsonPath("$._embedded.userDtoList.length()").value(2))
				.andExpect(jsonPath("$._links.self.href").exists());
	}

	@SneakyThrows
	@Test
	public void findUserById() {
		when(service.findUserById(any())).thenReturn(userDto).thenThrow(NotFoundException.class);
		when(assembler.toModel(userDto, RequestType.VIEW))
				.thenReturn(EntityModel.of(userDto)
						.add(Link.of("http://localhost/users/1", "self"))
						.add(Link.of("http://localhost/users/1", "update"))
						.add(Link.of("http://localhost/users/1", "delete")));

		mvc.perform(get("/users/{userId}", 1L)
						.characterEncoding(StandardCharsets.UTF_8)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))

				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name", is(userDto.getName())))
				.andExpect(jsonPath("$.age", is(userDto.getAge())))
				.andExpect(jsonPath("$.email", is(userDto.getEmail())))
				.andExpect(jsonPath("$._links.self.href").exists())
				.andExpect(jsonPath("$._links.update.href").exists())
				.andExpect(jsonPath("$._links.delete.href").exists());

		mvc.perform(get("/users/{userId}", 23)
						.characterEncoding(StandardCharsets.UTF_8)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))

				.andExpect(status().isNotFound());
	}
}