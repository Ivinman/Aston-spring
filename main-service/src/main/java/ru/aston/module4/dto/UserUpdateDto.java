package ru.aston.module4.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Schema(description = "Обновленные данные пользователя")
public class UserUpdateDto {
	@Schema(hidden = true)
	private long id;

	@Schema(description = "Имя пользователя", example = "NewName")
	@NotNull(message = "не может быть пустым")
	@NotBlank(message = "не может содержать только пробелы")
	@Size(min = 2, max = 255, message = "должно иметь 2–255 символов")
	private String name;

	@Schema(description = "Возраст пользователя", example = "30")
	@NotNull
	@Min(value = 0, message = "не может быть отрицательным")
	@Max(value = 120, message = "не может быть больше 120")
	private int age;

	@Schema(description = "Почта пользователя", example = "New@email.com")
	@NotNull(message = "не может быть пустым")
	@NotBlank(message = "не может содержать только пробелы")
	@Email
	private String email;

	@Override
	public String toString() {
		return String.format("Name: %s%nAge: %d%nEmail: %s%n", name, age, email);
	}
}
