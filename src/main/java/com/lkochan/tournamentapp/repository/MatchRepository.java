package com.lkochan.tournamentapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.lkochan.tournamentapp.entities.Match;

public interface MatchRepository extends JpaRepository<Match, Long> {
    @Query("SELECT m FROM Match m WHERE m.playerOne.id = :playerId OR m.playerTwo.id = :playerId")
    List<Match> findByPlayerId(Long playerId);
    List<Match> findByTournamentId(Long tournamentId);
}