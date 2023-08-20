package com.lkochan.tournamentapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lkochan.tournamentapp.entities.Tournament;

public interface TournamentRepository extends JpaRepository<Tournament, Long> {
    
}
