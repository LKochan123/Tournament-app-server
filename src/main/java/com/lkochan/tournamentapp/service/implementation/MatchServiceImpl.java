package com.lkochan.tournamentapp.service.implementation;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.lkochan.tournamentapp.entities.Match;
import com.lkochan.tournamentapp.entities.Player;
import com.lkochan.tournamentapp.entities.Tournament;
import com.lkochan.tournamentapp.exception.EntityNotFoundException;
import com.lkochan.tournamentapp.exception.EntityUtils;
import com.lkochan.tournamentapp.repository.MatchRepository;
import com.lkochan.tournamentapp.repository.PlayerRepository;
import com.lkochan.tournamentapp.repository.TournamentRepository;
import com.lkochan.tournamentapp.service.interfaces.MatchService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class MatchServiceImpl implements MatchService {

    MatchRepository matchRepository;
    PlayerRepository playerRepository;
    TournamentRepository tournamentRepository;

    private static final String message = "match";

    @Override
    public List<Match> getAllPlayersMatches(Long playerId) {
        return matchRepository.findByPlayerId(playerId);
    }

    @Override
    public List<Match> getAllTournamentMatches(Long tournamentId) {
        return matchRepository.findByTournamentId(tournamentId);
    }

    // @Override
    // public List<Match> getAllMatchesInRound(Long tournamentId, int roundNumber) {
    //     // TODO Auto-generated method stub
    //     throw new UnsupportedOperationException("Unimplemented method 'getAllMatchesInRound'");
    // }

    @Override
    public Match getMatch(Long id) {
        Optional<Match> match = matchRepository.findById(id);
        return EntityUtils.unwrapEntity(match, id, message);
    }

    @Override
    public void saveMatch(Match match, Long tournamentId, Long playerOneId, Long playerTwoId) {
        Optional<Tournament> tournament = tournamentRepository.findById(tournamentId);
        Optional<Player> playerOne = playerRepository.findById(playerOneId);
        Optional<Player> playerTwo = playerRepository.findById(playerTwoId);

        if (tournament.isPresent() && playerOne.isPresent() && playerTwo.isPresent()) {
            match.setTournament(tournament.get());
            match.setPlayerOne(playerOne.get());
            match.setPlayerTwo(playerTwo.get());
            matchRepository.save(match);
        } else {
            // TO DO! (change error name properly)
            throw new EntityNotFoundException("tournament", tournamentId);
        }
    }

    @Override
    public void updateMatch(Match match, Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateMatch'");
    }

    @Override
    public void deleteMatch(Long id) {
        Optional<Match> match = matchRepository.findById(id);
        if (match.isPresent()) {
            matchRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException(message, id);
        }
    }
    
}
