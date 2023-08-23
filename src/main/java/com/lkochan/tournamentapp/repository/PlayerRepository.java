package com.lkochan.tournamentapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lkochan.tournamentapp.entities.Player;

public interface PlayerRepository extends JpaRepository<Player, Long> {
    Optional<List<Player>> findByTournamentId(Long tournamentId);
}
