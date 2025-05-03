package ru.aston.module4.service;

import ru.aston.module4.dto.UserDto;
import ru.aston.module4.dto.UserModel;

import java.util.List;

public interface UserService {
    UserDto createUser(UserDto userDto);
    UserDto updateUser(Long userId, UserModel userModel);
    void deleteUser(Long userId);
    List<UserDto> findAllUsers();
    UserDto findUserById(Long userId);
}
