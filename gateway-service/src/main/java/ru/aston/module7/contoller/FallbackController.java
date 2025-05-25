package ru.aston.module7.contoller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class FallbackController {

	@RequestMapping("/fallback/user/**")
	public ResponseEntity<Map<String, Object>> fallback(HttpServletRequest request) {
		Map<String, Object> response = new HashMap<>();
		response.put("error", "UserService недоступен, попробуйте позднее");
		response.put("path", request.getRequestURI());
		return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
	}

}


