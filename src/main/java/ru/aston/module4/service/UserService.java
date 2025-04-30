package ru.aston.module4.service;

import ru.aston.module4.dto.UserDto;

import java.util.List;

public interface UserService {
    String createUser(UserDto userDto) throws Exception;
    String updateUser(Integer userId, UserDto userDto) throws Exception;
    String deleteUSer(Integer userId);
    List<UserDto> findAllUsers();
    UserDto findUserById(Integer userId);
}
