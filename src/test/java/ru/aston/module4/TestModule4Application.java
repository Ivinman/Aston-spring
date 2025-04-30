package ru.aston.module4;

import org.springframework.boot.SpringApplication;

public class TestModule4Application {

	public static void main(String[] args) {
		SpringApplication.from(Module4Application::main).with(TestcontainersConfiguration.class).run(args);
	}

}
