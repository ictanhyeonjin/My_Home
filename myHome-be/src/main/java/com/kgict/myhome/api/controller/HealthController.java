package com.kgict.myhome.api.controller;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HealthController {

	@GetMapping("/health")
	public Map<String, Object> health() {
		return Map.of(
				"status", "UP",
				"service", "myHome",
				"timestamp", LocalDateTime.now()
		);
	}

	@GetMapping("/hello")
	public Map<String, String> hello() {
		return Map.of("message", "hello myHome");
	}
}
