package ru.aston.module4.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.aston.module4.dto.UserDto;
import ru.aston.module4.dto.UserUpdateDto;
import ru.aston.module4.entity.User;
import ru.aston.module4.exception.NotFoundException;
import ru.aston.module4.mapper.UserMapper;
import ru.aston.module4.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    @Autowired
    private final UserMapper mapper;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {
        User user = mapper.toUser(userDto);
        User savedUser = userRepository.save(user);
        log.info("New user with id {} successfully created", savedUser.getId());
        return mapper.toDto(savedUser);
    }

    @Override
    @Transactional
    public UserDto updateUser(Long userId, UserUpdateDto userUpdateDto) {
        User user = getUserOrThrow(userId);
        mapper.updateUserFromDto(userUpdateDto, user);
        User userToUpdate = userRepository.save(user);
        log.info("User with id {} successfully updated", userToUpdate.getId());
        return mapper.toDto(userToUpdate);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        getUserOrThrow(userId);
        userRepository.deleteById(userId);
        log.info("User with id {} successfully deleted", userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> findAllUsers() {
        return mapper.toDtoList(userRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto findUserById(Long userId) {
        return mapper.toDto(getUserOrThrow(userId));
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with id %d not found", userId)));
    }
}

