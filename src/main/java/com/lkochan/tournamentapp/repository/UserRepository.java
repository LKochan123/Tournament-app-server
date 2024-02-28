package com.lkochan.tournamentapp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lkochan.tournamentapp.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
