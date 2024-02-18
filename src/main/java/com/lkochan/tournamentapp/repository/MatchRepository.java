package com.lkochan.tournamentapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.lkochan.tournamentapp.entities.Match;

public interface MatchRepository extends JpaRepository<Match, Long> {

    @Query("SELECT m FROM Match m WHERE m.playerOne.id = :playerId OR m.playerTwo.id = :playerId")
    Optional<List<Match>> findByPlayerId(Long playerId);

    @Query("SELECT m FROM Match m " +
       "WHERE (m.playerOne.id = :playerOneId AND m.playerTwo.id = :playerTwoId) " +
       "OR (m.playerOne.id = :playerTwoId AND m.playerTwo.id = :playerOneId)"
    )
    Optional<List<Match>> findHeadToHeadMatches(Long playerOneId, Long playerTwoId);
    
    Optional<List<Match>> findByTournamentId(Long tournamentId);
}
