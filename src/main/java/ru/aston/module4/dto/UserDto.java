package ru.aston.module4.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Email;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class UserDto {
    private Long id;

    @NotNull(message = "не может быть пустым")
    @NotBlank(message = "не может содержать только пробелы")
    @Size(min = 2, max = 255, message = "должно иметь 2–255 символов")
    private String name;

    @NotNull
    @Min(value = 0, message = "не может быть отрицательным")
    @Max(value = 120, message = "не может быть больше 120")
    private int age;

    @NotNull(message = "не может быть пустым")
    @NotBlank(message = "не может содержать только пробелы")
    @Email
    private String email;

    private LocalDateTime createdAt;

    @Override
    public String toString() {
        return String.format("Name: %s%nAge: %d%nEmail: %s%n", name, age, email);
    }
}
