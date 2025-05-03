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
import ru.aston.module4.dto.UserModel;
import ru.aston.module4.exception.AlreadyExistException;
import ru.aston.module4.exception.NotFoundException;
import ru.aston.module4.service.UserService;
import ru.aston.module4.springRep.UserRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
public class UserServiceTest {
    @Autowired
    private final UserService service;
    private final UserRepository repository;

    private final UserDto userDto1 = new UserDto("test", 23, "test@test.ru");
    private final UserDto userDto2 = new UserDto("testSecond", 32, "testSecond@test.ru");

    @Test
    public void addUser() {
        service.createUser(userDto1);
        assertEquals(1, service.findAllUsers().size());
        assertEquals(repository.findByEmail(userDto1.getEmail()).getEmail(), userDto1.getEmail());
        assertThrows(AlreadyExistException.class, () -> service.createUser(userDto1));
    }

    @Test
    public void updateUser() {
        service.createUser(userDto1);
        UserModel userModel = new UserModel(2L, "boris", 23, "testUpdate@test.ru");
        service.updateUser(repository.findAll().get(0).getId(), userModel);
        Assertions.assertNotNull(repository.findByEmail(userModel.getEmail()));

        service.createUser(userDto2);
        assertThrows(NotFoundException.class, () -> service.updateUser(23L, userModel));

        service.updateUser(repository.findAll().get(1).getId(), userModel);
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
