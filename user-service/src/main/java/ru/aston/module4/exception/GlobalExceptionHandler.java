package ru.aston.module4.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(NotFoundException.class)
	public ResponseEntity<String> handleNotFound(NotFoundException ex, HttpServletRequest request) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
	}

	@ExceptionHandler(AlreadyExistException.class)
	public ResponseEntity<String> handleAlreadyExists(AlreadyExistException ex, HttpServletRequest request) {
		return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<String> handleGenericException(Exception ex, HttpServletRequest request) {
		log.error("Exception occurred while handling request at {}: {}", request.getRequestURI(), ex.getMessage(), ex);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ошибка: " + ex.getMessage());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ValidationErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
		List<ValidationErrorResponse.Violation> violations = ex.getBindingResult().getFieldErrors().stream()
				.map(error -> {
					ValidationErrorResponse.Violation violation = new ValidationErrorResponse.Violation();
					violation.setField(error.getField());
					violation.setMessage(error.getDefaultMessage());
					return violation;
				})
				.collect(Collectors.toList());

		ValidationErrorResponse response = new ValidationErrorResponse("Ошибка валидации", violations);

		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(response);
	}
}
