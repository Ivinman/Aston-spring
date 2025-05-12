package ru.aston.module4;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;
import ru.aston.module4.dto.UserDto;
import ru.aston.module4.dto.UserEventDto;
import ru.aston.module4.dto.UserUpdateDto;
import ru.aston.module4.exception.NotFoundException;
import ru.aston.module4.repository.UserRepository;
import ru.aston.module4.service.UserService;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
@Testcontainers
public class UserServiceTest {
    private final UserService service;
    private final UserRepository repository;

    private final UserDto userDto1 = UserDto.builder()
            .name("test")
            .age(23)
            .email("test@test.ru")
            .build();

    private final UserDto userDto2 = UserDto.builder()
            .name("testSecond")
            .age(32)
            .email("testSecond@test.ru")
            .build();

    @Container
    private static final KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("apache/kafka-native:latest"));

    @DynamicPropertySource
    private static void setProperty(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
    }

    private ConsumerRecords<String, UserEventDto> getConsumerRecords() {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers());
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "testGroupId");
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        properties.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);
        properties.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        Consumer<String, UserEventDto> consumer = new KafkaConsumer<>(properties, new StringDeserializer(),
                new JsonDeserializer<>(UserEventDto.class));
        consumer.subscribe(Collections.singletonList("userEventTopic"));
        ConsumerRecords<String, UserEventDto> records = consumer.poll(Duration.ofMillis(10000));
        consumer.close();
        return records;
    }

    @Test
    public void addUser() {
        service.createUser(userDto1);
        assertEquals(1, service.findAllUsers().size());
        assertEquals(repository.findByEmail(userDto1.getEmail()).getEmail(), userDto1.getEmail());
        assertThrows(DataIntegrityViolationException.class, () -> service.createUser(userDto1));
        assertEquals(1, getConsumerRecords().count());
    }

    @Test
    public void updateUser() {
        service.createUser(userDto1);
        UserUpdateDto userUpdateDto = new UserUpdateDto(2L, "boris", 23, "testUpdate@test.ru");
        service.updateUser(repository.findAll().get(0).getId(), userUpdateDto);
        Assertions.assertNotNull(repository.findByEmail(userUpdateDto.getEmail()));

        service.createUser(userDto2);
        assertThrows(NotFoundException.class, () -> service.updateUser(23L, userUpdateDto));

        service.updateUser(repository.findAll().get(1).getId(), userUpdateDto);
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
        assertEquals(7, getConsumerRecords().count());
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
