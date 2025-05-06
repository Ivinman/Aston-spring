package ru.aston.module4.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserNotificationMessage {
	private final Event event;
	private final String email;
	private String message;

	public static NotificationBuilder builder(String email, String userName) {
		return new NotificationBuilder(email, userName);
	}

	@AllArgsConstructor
	public static class NotificationBuilder {
		private final String email;
		private final String userName;

		public UserNotificationMessage userCreateNotification() {
			return new UserNotificationMessage(
					Event.CREATE,
					email,
					"Здравствуйте!\nВаш аккаунт " + userName + " на сайте User Service был успешно создан."
			);
		}

		public UserNotificationMessage userDeleteNotification() {
			return new UserNotificationMessage(
					Event.DELETE,
					email,
					"Здравствуйте!\nВаш аккаунт " + userName + " на сайте User Service был успешно удалён."
			);
		}
	}

	private enum Event {
		CREATE,
		DELETE
	}
}

