package com.lkochan.tournamentapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.lkochan.tournamentapp.entities.Player;

public interface PlayerRepository extends JpaRepository<Player, Long> {
    @Query("SELECT p FROM Player p WHERE p.tournament.id = ?1 ORDER BY p.scoredPoints DESC, p.lostPoints ASC")
    Optional<List<Player>> findByTournamentIdAndOrderByPoints(Long tournamentId);
}
