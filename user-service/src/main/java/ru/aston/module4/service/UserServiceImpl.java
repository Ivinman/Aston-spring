package ru.aston.module4.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.aston.module4.dto.UserDto;
import ru.aston.module4.dto.UserEventDto;
import ru.aston.module4.dto.UserUpdateDto;
import ru.aston.module4.entity.User;
import ru.aston.module4.exception.AlreadyExistException;
import ru.aston.module4.exception.NotFoundException;
import ru.aston.module4.mapper.UserMapper;
import ru.aston.module4.repository.UserRepository;

import java.util.List;

/**
 * Базовая реализация {@link UserService} через @see ru.aston.module4.repository.UserRepository
 * c внутренним методом @see #getUserOrThrow(Long)
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserMapper mapper;
    private final UserRepository userRepository;
    private final KafkaProducerService kafkaProducer;

    /**
     * Создает запись в базе данных на основе полученного userDto
     * и отправляет {@link ru.aston.module4.dto.UserEventDto} в userEventTopic kafka
     * <p>Так же создает log</p>
     * @param userDto основа для создания записи
     * @return ту же запись userDto с присвоенным id
     */

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {
        checkIfExist(userDto.getEmail());
        User user = mapper.toUser(userDto);
        User savedUser = userRepository.save(user);
        UserEventDto userCreateDto = new UserEventDto(user.getName(), userDto.getEmail(), UserEventDto.Event.CREATE);
        kafkaProducer.sendUserEvent(userCreateDto);
        log.info("New user with id {} successfully created", savedUser.getId());
        return mapper.toDto(savedUser);
    }

    /**
     * Обновляет запись на основе полученного id и userUpdateDto
     * <p>Так же создает log</p>
     * @param userId ключ для поиска записи в базе данных
     * @param userUpdateDto описывает поля, которые будут обновлены
     * @return обновленную запись userDto
     */

    @Override
    @Transactional
    public UserDto updateUser(Long userId, UserUpdateDto userUpdateDto) {
        User user = getUserOrThrow(userId);
        checkIfExist(userUpdateDto.getEmail());
        mapper.updateUserFromDto(userUpdateDto, user);
        User userToUpdate = userRepository.save(user);
        log.info("User with id {} successfully updated", userToUpdate.getId());
        return mapper.toDto(userToUpdate);
    }

    /**
     * Удаляет из базы данных запись с полученным ключем
     * и отправляет {@link ru.aston.module4.dto.UserEventDto} в userEventTopic kafka
     * <p>Так же создает log</p>
     * @param userId ключ для удаления
     */

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        User user = getUserOrThrow(userId);
        userRepository.deleteById(userId);

        UserEventDto userDeleteDto = new UserEventDto(user.getName(), user.getEmail(), UserEventDto.Event.DELETE);

        kafkaProducer.sendUserEvent(userDeleteDto);

        log.info("User with id {} successfully deleted", userId);
    }

    /**
     * Ищет всех пользователей в базе данных
     * или выбрасывает исключение
     * @return список @see ru.aston.module4.dto.UserDto
     * @throws NotFoundException если список пуст
     */

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> findAllUsers() {
        List<User> users = userRepository.findAll();
        if (!users.isEmpty()) {
            return mapper.toDtoList(users);
        } else {
            throw new NotFoundException("No users found in database");
        }
    }

    /**
     * Ищет пользователя с указанным ключем через метод
     * @see #getUserOrThrow(Long)
     * @param userId ключ поиска
     * @return запись userDto
     */

    @Override
    @Transactional(readOnly = true)
    public UserDto findUserById(Long userId) {
        return mapper.toDto(getUserOrThrow(userId));
    }

    /**
     * Ищет пользователя с указанным ключем а базе данных
     * @param userId ключ поиска
     * @return запись user
     * @throws NotFoundException если записи с указанным ключем не существует
     */

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with id %d not found", userId)));
    }

    private void checkIfExist(String email) {
        if (userRepository.findByEmail(email) != null) {
            throw new AlreadyExistException(String.format("Email %s already used", email));
        }
    }
}

