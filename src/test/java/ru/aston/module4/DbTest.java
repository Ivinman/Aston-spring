package ru.aston.module4;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;
import ru.aston.module4.dto.UserDto;
import ru.aston.module4.model.User;
import ru.aston.module4.service.UserService;
import ru.aston.module4.springRep.UserRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
public class DbTest {
    private final UserService service;
    private final UserRepository repository;

    @SneakyThrows
    @Order(value = 1)
    @Test
    public void addUser() {
        UserDto userDto = new UserDto("test", 23, "test@test.ru");
        service.createUser(userDto);
        assertEquals(1, service.findAllUsers().size());
    }

    @Order(value = 2)
    @Test
    public void checkDb() {
        assertEquals(0, service.findAllUsers().size());
    }

    @Order(value = 3)
    @Test
    public void db() {
        UserDto userDto = new UserDto("test", 23, "test@test.ru");
        User user = new User(userDto);
        repository.save(user);
        assertEquals(1, repository.findAll().size());
    }
}
