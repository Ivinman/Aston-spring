package ru.aston.module4.service;

import jakarta.validation.Valid;
import ru.aston.module4.dto.UserDto;
import ru.aston.module4.dto.UserModel;

import java.util.List;
import java.util.Optional;

public interface UserService {
    String createUser(@Valid UserDto userDto) throws Exception;
    String updateUser(Integer userId, @Valid UserModel userModel) throws Exception;
    String deleteUSer(Integer userId);
    List<UserDto> findAllUsers();
    Optional<UserDto> findUserById(Integer userId);
}
