package ru.aston.module4.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.aston.module4.dto.UserDto;
import ru.aston.module4.dto.UserUpdateDto;
import ru.aston.module4.mapper.RequestType;
import ru.aston.module4.mapper.UserModelAssembler;
import ru.aston.module4.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Tag(name = "Пользователи", description = "Основные методы при работе с пользователями")
public class UserController {

    private final UserModelAssembler assembler;
    private final UserService userService;

    @PostMapping
    @Operation(summary = "Добавить нового пользователя", responses = {@ApiResponse(responseCode = "201", description = "Успешная операция добавления"),
            @ApiResponse(responseCode = "400", description = "Ошибка при вводе данных", content = @Content),
            @ApiResponse(responseCode = "409", description = "Использование уже существующей почты", content = @Content)})
    public ResponseEntity<EntityModel<UserDto>> addUser(@Parameter(description = "Данные нового пользователя")
                                                            @RequestBody @Valid UserDto userDto) {
        UserDto createdUser = userService.createUser(userDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(assembler.toModel(createdUser, RequestType.CREATE));
    }

    @PatchMapping("/{userId}")
    @Operation(summary = "Обновить данные пользователя по id", responses = {@ApiResponse(responseCode = "200", description = "Успешная операция"),
            @ApiResponse(responseCode = "400", description = "Ошибка при вводе данных", content = @Content),
            @ApiResponse(responseCode = "404", description = "Пользователь с данным id не найден", content = @Content),
            @ApiResponse(responseCode = "409", description = "Использование уже существующей почты", content = @Content)})
    public ResponseEntity<EntityModel<UserDto>> updateUser(@Parameter(description = "Id пользователя")
                                                               @Positive @PathVariable Long userId,
                                              @Parameter(description = "Новые данные пользователя")
                                              @RequestBody @Valid UserUpdateDto userUpdateDto) {
        return ResponseEntity.ok(assembler.toModel(userService.updateUser(userId, userUpdateDto), RequestType.UPDATE));
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "Удалить пользователя по id", responses = {@ApiResponse(responseCode = "204", description = "Успешная операция удаления"),
            @ApiResponse(responseCode = "400", description = "Ошибка при вводе данных", content = @Content),
            @ApiResponse(responseCode = "404", description = "Пользователь с данным id не найден", content = @Content)})
    public ResponseEntity<Void> deleteUser(@Parameter(description = "Id пользователя")
                                               @Positive @PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "Получить список всех пользователей", responses = @ApiResponse(responseCode = "200", description = "Успешная операция"))
    public ResponseEntity<CollectionModel<EntityModel<UserDto>>> findAllUsers() {
        List<EntityModel<UserDto>> users = userService.findAllUsers().stream()
                .map(user -> assembler.toModel(user, RequestType.LIST))
                .collect(Collectors.toList());
        return ResponseEntity.ok(
                CollectionModel.of(users, linkTo(methodOn(UserController.class).findAllUsers()).withSelfRel())
        );
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Получить пользователя по id", responses = {@ApiResponse(responseCode = "200", description = "Успешная операция"),
            @ApiResponse(responseCode = "400", description = "Ошибка при вводе данных", content = @Content),
            @ApiResponse(responseCode = "404", description = "Пользователь с данным id не найден", content = @Content)})
    public ResponseEntity<EntityModel<UserDto>> findUserById(@Parameter(description = "Id пользователя")
                                                                 @Positive @PathVariable Long userId) {
        return ResponseEntity.ok(assembler.toModel(userService.findUserById(userId), RequestType.VIEW));
    }
}

