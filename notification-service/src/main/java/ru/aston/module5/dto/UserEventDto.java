package ru.aston.module5.dto;

public record UserEventDto(
        String username,
        String email,
        Event event
) {
    public enum Event {
        CREATE,
        DELETE
    }
}
