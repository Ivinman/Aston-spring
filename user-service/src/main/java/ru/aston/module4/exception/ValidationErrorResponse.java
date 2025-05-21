package ru.aston.module4.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Schema(description = "Ответ при ошибке валидации")
@Setter
@Getter
@AllArgsConstructor
public class ValidationErrorResponse {

	@Schema(description = "Cообщение об ошибке", example = "Validation failed")
	private String message;

	@Schema(description = "Поля с ошибками")
	private List<Violation> violations;

	@Schema(description = "Ошибка конкретного поля")
	@Setter
	@Getter
	public static class Violation {

		@Schema(description = "Поле, вызвавшее ошибку", example = "email")
		private String field;

		@Schema(description = "Сообщение об ошибке", example = "must be a valid email address")
		private String message;
	}
}

