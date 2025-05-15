package ru.aston.module4.controller;

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
public class UserController {

    private final UserModelAssembler assembler;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<EntityModel<UserDto>> addUser(@RequestBody @Valid UserDto userDto) {
        UserDto createdUser = userService.createUser(userDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(assembler.toModel(createdUser, RequestType.CREATE));
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<EntityModel<UserDto>> updateUser(@Positive @PathVariable Long userId,
                                              @RequestBody @Valid UserUpdateDto userUpdateDto) {
        return ResponseEntity.ok(assembler.toModel(userService.updateUser(userId, userUpdateDto), RequestType.UPDATE));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@Positive @PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<UserDto>>> findAllUsers() {
        List<EntityModel<UserDto>> users = userService.findAllUsers().stream()
                .map(user -> assembler.toModel(user, RequestType.LIST))
                .collect(Collectors.toList());
        return ResponseEntity.ok(
                CollectionModel.of(users, linkTo(methodOn(UserController.class).findAllUsers()).withSelfRel())
        );
    }

    @GetMapping("/{userId}")
    public ResponseEntity<EntityModel<UserDto>> findUserById(@Positive @PathVariable Long userId) {
        return ResponseEntity.ok(assembler.toModel(userService.findUserById(userId), RequestType.VIEW));
    }
}

