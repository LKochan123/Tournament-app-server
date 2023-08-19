package com.lkochan.tournamentapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lkochan.tournamentapp.entities.Tournament;

@Repository
public interface TournamentRepository extends JpaRepository<Tournament, Long> {
    
}
