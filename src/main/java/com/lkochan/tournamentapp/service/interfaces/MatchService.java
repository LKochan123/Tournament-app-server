package com.lkochan.tournamentapp.service.interfaces;

import java.util.List;

import com.lkochan.tournamentapp.entities.Match;

public interface MatchService {
    List<Match> getAllPlayersMatches(Long playerId);
    List<Match> getAllTournamentMatches(Long tournamentId);
    // List<Match> getAllMatchesInRound(Long tournamentId, int roundNumber);
    Match getMatch(Long id);
    // void advanceToNextRound(Long tournamentId, int currentRound);
    void saveMatch(Match match, Long tournamentId, Long playerOneId, Long playerTwoId);
    void updateMatch(Match match, Long id);
    void deleteMatch(Long id);
}
