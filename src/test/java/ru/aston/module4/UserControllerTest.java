package ru.aston.module4;

import lombok.SneakyThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import ru.aston.module4.dto.UserDto;
import ru.aston.module4.dto.UserModel;
import ru.aston.module4.entity.User;
import ru.aston.module4.exception.AlreadyExistException;
import ru.aston.module4.exception.NotFoundException;
import ru.aston.module4.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Transactional
@SpringBootTest
class UserControllerTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private final UserService service = mock();

    private MockMvc mvc;
    private final User user = new User();
    private final UserDto userDto = new UserDto("test", 23, "test@email.ru");
    private final UserDto badUserDto = new UserDto("t", -4, "t st@");
    private final UserModel userModel = new UserModel(2L, "testSecond", 23, "testSecond@email.ru");

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
        mvc.perform(post("/users")
                .content(objectMapper.writeValueAsString(userDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())))
                .andExpect(jsonPath("$.age", is(userDto.getAge())));

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
        when(service.updateUser(any(), any())).thenReturn(new UserDto(
                userModel.getName(),
                userModel.getAge(),
                userModel.getEmail()))
                .thenThrow(NotFoundException.class)
                .thenThrow(AlreadyExistException.class);

        mvc.perform(patch("/users/{userId}" , 1L)
                .content(objectMapper.writeValueAsString(userModel))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(userModel.getName())))
                .andExpect(jsonPath("$.age", is(userModel.getAge())))
                .andExpect(jsonPath("$.email", is(userModel.getEmail())));

        mvc.perform(patch("/users/{userId}" , 1)
                        .content(objectMapper.writeValueAsString(badUserDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isBadRequest());

        mvc.perform(patch("/users/{userId}" , 23)
                .content(objectMapper.writeValueAsString(userModel))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))

                        .andExpect(status().isNotFound());

        mvc.perform(patch("/users/{userId}" , 1)
                .content(objectMapper.writeValueAsString(userModel))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))

                        .andExpect(status().isConflict());
    }

    @SneakyThrows
    @Test
    public void deleteUser() {
        mvc.perform(delete("/users/{userId}" , 1L)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isNoContent());
        verify(service, times(1)).deleteUser(any());

        doThrow(new NotFoundException("not found")).when(service).deleteUser(any());
        mvc.perform(delete("/users/{userId}" , 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    public void findAllUsers() {
        when(service.findAllUsers()).thenReturn(List.of(userDto, badUserDto));
        mvc.perform(get("/users")
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @SneakyThrows
    @Test
    public void findUserById() {
        when(service.findUserById(any())).thenReturn(userDto).thenThrow(NotFoundException.class);
        mvc.perform(get("/users/{userId}", 1L)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.age", is(userDto.getAge())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));

        mvc.perform(get("/users/{userId}", 23)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isNotFound());
    }
}