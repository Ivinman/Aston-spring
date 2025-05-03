package ru.aston.module4.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.aston.module4.exception.AlreadyExistException;
import ru.aston.module4.exception.NotFoundException;
import ru.aston.module4.dto.UserDto;
import ru.aston.module4.dto.UserModel;
import ru.aston.module4.mapper.UserMapper;
import ru.aston.module4.entity.User;
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
	public UserDto createUser(UserDto userDto) {
			User user = mapper.toUser(userDto);
			User saved = userRepository.save(user);
			return mapper.toDto(saved);
	}

	@Override
	@Transactional
	public UserDto updateUser(Long userId, @Valid UserModel userModel) {
		User user = getUserOrThrow(userId);

		try {
			mapper.updateUserFromDto(userModel, user);
			User updated = userRepository.save(user);
			return mapper.toDto(updated);
		} catch (DataIntegrityViolationException e) {
			log.error("Ошибка во время обновления пользователя {} {}", user, e.getMessage());
			throw new AlreadyExistException("Пользователь с такой почтой уже существует");
		}
	}

	@Override
	@Transactional
	public void deleteUser(Long userId) {
		if (!userRepository.existsById(userId)) {
			log.error("Пользователь c id = {} не найден", userId);
			throw new NotFoundException("Пользователь с id = " + userId + " не существует");
		}
		userRepository.deleteById(userId);
	}

	@Override
	@Transactional(readOnly = true)
	public List<UserDto> findAllUsers() {
		return mapper.toDtoList(userRepository.findAll());
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<UserDto> findUserById(Long userId) {
		return userRepository.findById(userId)
				.map(mapper::toDto);
	}

	private User getUserOrThrow(Long userId) {
		return userRepository.findById(userId)
				.orElseThrow(() -> {
					log.error("Пользователь c id = {} не найден", userId);
					return new NotFoundException("Пользователь с id = " + userId + " не существует");
				});
	}
}

