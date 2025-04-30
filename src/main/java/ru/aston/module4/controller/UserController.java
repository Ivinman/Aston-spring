package ru.aston.module4.controller;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

import ru.aston.module4.dto.UserDto;
import ru.aston.module4.service.UserService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public String addUser(@RequestBody @Valid UserDto userDto) throws Exception {
        return userService.createUser(userDto);
    }

    @PatchMapping("/{userId}")
    public String updateUser(@PathVariable Integer userId,
                           @RequestBody @Valid UserDto userDto) throws Exception {
        return userService.updateUser(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public String deleteUser(@PathVariable Integer userId) {
        return userService.deleteUSer(userId);
    }

    @GetMapping
    public List<UserDto> findAllUsers() {
        return userService.findAllUsers();
    }

    @GetMapping("/{userId}")
    public UserDto findUserById(@PathVariable("userId") Integer userId) {
        return userService.findUserById(userId);
    }
}
