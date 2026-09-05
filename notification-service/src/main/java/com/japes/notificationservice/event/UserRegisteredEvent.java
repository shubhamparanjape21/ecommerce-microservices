package com.japes.notificationservice.event;

public record UserRegisteredEvent(Long userId, String name, String email) {

}
