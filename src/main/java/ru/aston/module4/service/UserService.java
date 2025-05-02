package ru.aston.module4.service;

import jakarta.validation.Valid;
import ru.aston.module4.dto.UserDto;
import ru.aston.module4.dto.UserModel;

import java.util.List;
import java.util.Optional;

public interface UserService {
    UserDto createUser(@Valid UserDto userDto);
    UserDto updateUser(Long userId, @Valid UserModel userModel);
    void deleteUser(Long userId);
    List<UserDto> findAllUsers();
    Optional<UserDto> findUserById(Long userId);
}
