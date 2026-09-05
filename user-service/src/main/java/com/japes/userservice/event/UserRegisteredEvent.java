package com.japes.userservice.event;

public record UserRegisteredEvent(Long userId, String name, String email) {

}
