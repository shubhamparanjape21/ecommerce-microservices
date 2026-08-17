package com.japes.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.japes.userservice.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

}
