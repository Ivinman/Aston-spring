package ru.aston.module5;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class NotificationApp {
	public static void main(String[] args) {
		SpringApplication.run(NotificationApp.class, args);
	}

}
