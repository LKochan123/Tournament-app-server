package com.lkochan.tournamentapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lkochan.tournamentapp.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {
    
}
