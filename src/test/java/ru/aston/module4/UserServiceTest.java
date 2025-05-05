package ru.aston.module4;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;
import ru.aston.module4.dto.UserDto;
import ru.aston.module4.dto.UserUpdateDto;
import ru.aston.module4.exception.NotFoundException;
import ru.aston.module4.repository.UserRepository;
import ru.aston.module4.service.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
public class UserServiceTest {
    private final UserService service;
    private final UserRepository repository;

    private final UserDto userDto1 = UserDto.builder()
            .name("test")
            .age(23)
            .email("test@test.ru")
            .build();

    private final UserDto userDto2 = UserDto.builder()
            .name("testSecond")
            .age(32)
            .email("testSecond@test.ru")
            .build();

    @Test
    public void addUser() {
        service.createUser(userDto1);
        assertEquals(1, service.findAllUsers().size());
        assertEquals(repository.findByEmail(userDto1.getEmail()).getEmail(), userDto1.getEmail());
        assertThrows(DataIntegrityViolationException.class, () -> service.createUser(userDto1));
    }

    @Test
    public void updateUser() {
        service.createUser(userDto1);
        UserUpdateDto userUpdateDto = new UserUpdateDto(2L, "boris", 23, "testUpdate@test.ru");
        service.updateUser(repository.findAll().get(0).getId(), userUpdateDto);
        Assertions.assertNotNull(repository.findByEmail(userUpdateDto.getEmail()));

        service.createUser(userDto2);
        assertThrows(NotFoundException.class, () -> service.updateUser(23L, userUpdateDto));

        service.updateUser(repository.findAll().get(1).getId(), userUpdateDto);
        assertThrows(DataIntegrityViolationException.class, repository::findAll);
    }

    @Test
    public void deleteUser() {
        service.createUser(userDto1);
        service.createUser(userDto2);
        assertEquals(2, repository.findAll().size());
        service.deleteUser(repository.findAll().get(0).getId());
        assertEquals(1, repository.findAll().size());
        assertThrows(NotFoundException.class, () -> service.deleteUser(23L));
    }

    @Test
    public void findAllUsers() {
        assertEquals(0, repository.findAll().size());
        service.createUser(userDto1);
        service.createUser(userDto2);
        assertEquals(2, service.findAllUsers().size());
    }

    @Test
    public void findUserById() {
        service.createUser(userDto1);
        assertThrows(NotFoundException.class, () -> service.findUserById(23L));
        service.createUser(userDto2);
        assertEquals(2, repository.findAll().size());

        assertEquals(service.findUserById(repository.findAll().get(0).getId()).getEmail(), userDto1.getEmail());
    }

}
