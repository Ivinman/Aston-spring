package ru.aston.module4.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import org.springframework.validation.annotation.Validated;
import ru.aston.module4.Exception.AlreadyExistException;
import ru.aston.module4.Exception.NotFoundException;
import ru.aston.module4.dto.UserDto;
import ru.aston.module4.dto.UserModel;
import ru.aston.module4.mapper.UserMapper;
import ru.aston.module4.model.User;
import ru.aston.module4.springRep.UserRepository;


import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Validated
public class UserServiceImpl implements UserService {
    @Autowired
    private final UserMapper mapper;
    private final UserRepository userRepository;

    @Override
    public String createUser(@Valid UserDto userDto) throws Exception {
        try {
            User user = userRepository.save(mapper.toUser(userDto));
            return "Пользователь " + user.getName() + " добавлен в базу данных с id = " + user.getId();
        } catch (Exception e) {
            throw new AlreadyExistException("Пользователь с такой почтой уже существует");
        }
    }

    @Override
    public String updateUser(Integer userId, @Valid UserModel userModel) throws Exception {
        Optional<User> userFromDb = userRepository.findById(userId);
        if (userFromDb.isEmpty()) {
            throw new NotFoundException("Пользователь с id = " + userId + " не существует");
        }

        try {
            User user = userFromDb.get();
            mapper.updateUserFromDto(userModel, user);
            userRepository.save(user);
            return "Данные пользователя с id = " + userId + " обновлены";

        } catch (DataIntegrityViolationException e) {
            throw new AlreadyExistException("Пользователь с такой почтой уже существует");
        }
    }


    @Override
    public String deleteUSer(Integer userId) {
        try {
            userRepository.deleteById(userId);
            return "Пользователь с id = " + userId + " удален";
        } catch (Exception e) {
            throw new NotFoundException("Пользователь с id = " + userId + " не существует");
        }
    }

    @SneakyThrows
    @Override
    public List<UserDto> findAllUsers() {
        return userRepository.findAll().stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public Optional<UserDto> findUserById(Integer userId) {
        Optional<User> userFromDb = userRepository.findById(userId);
	    return userFromDb.map(mapper::toDto);
    }
}
