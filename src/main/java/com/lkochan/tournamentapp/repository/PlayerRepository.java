package com.lkochan.tournamentapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lkochan.tournamentapp.entities.Player;

public interface PlayerRepository extends JpaRepository<Player, Long> {
    List<Player> findByTournamentId(Long tournament_id);
}
