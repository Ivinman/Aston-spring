package ru.aston.module4.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import ru.aston.module4.Exception.AlreadyExistException;
import ru.aston.module4.Exception.NotFoundException;
import ru.aston.module4.dto.UserDto;
import ru.aston.module4.model.User;
import ru.aston.module4.springRep.UserRepository;


import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public String createUser(UserDto userDto) throws Exception {
        try {
            User user = new User(userDto);
            userRepository.save(user);
            return "Пользователь добавлен";
        } catch (Exception e) {
            throw new AlreadyExistException("Пользователь с такой почтой уже существует");
        }
    }

    @Override
    public String updateUser(Integer userId, UserDto userDto) throws Exception {
        Optional<User> userFromDb = userRepository.findById(userId);
        if (userFromDb.isEmpty()) {
            throw new NotFoundException("Пользователя с id = " + userId + " не существует");
        }
        try {
            User user = userFromDb.get();
            user.setName(userDto.getName());
            user.setEmail(userDto.getEmail());
            user.setAge(userDto.getAge());
            userRepository.save(user);
            return "Данные пользователя с id = " + userId + " обновлены";
        } catch (Exception e) {
            throw new AlreadyExistException("Пользователь с такой почтой уже существует");
        }
    }

    @Override
    public String deleteUSer(Integer userId) {
        try {
            userRepository.deleteById(userId);
            return "Пользователя с id = " + userId + " удален";
        } catch (Exception e) {
            throw new NotFoundException("Пользователя с id = " + userId + " не существует");
        }
    }

    @SneakyThrows
    @Override
    public List<UserDto> findAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> new UserDto(user.getName(), user.getAge(), user.getEmail()))
                .toList();
    }

    @Override
    public UserDto findUserById(Integer userId) {
        Optional<User> userFromDb = userRepository.findById(userId);
        if (userFromDb.isEmpty()) {
            throw new NotFoundException("Пользователя с id = " + userId + " не существует");
        }
        return new UserDto(userFromDb.get().getName(), userFromDb.get().getAge(), userFromDb.get().getEmail());
    }
}
