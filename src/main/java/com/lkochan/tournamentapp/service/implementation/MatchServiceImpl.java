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
        Optional<List<Match>> matches = matchRepository.findByPlayerId(playerId);
        return EntityUtils.unwrapEntity(matches, playerId, "player");
    }

    @Override
    public List<Match> getAllTournamentMatches(Long tournamentId) {
        Optional<List<Match>> matches = matchRepository.findByTournamentId(tournamentId);
        return EntityUtils.unwrapEntity(matches, tournamentId, "tournament");
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

            updatePlayerDetails(playerOne.get(), match, "playerOne");
            updatePlayerDetails(playerTwo.get(), match, "playerTwo");

            matchRepository.save(match);
        } else {
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

    private void updatePlayerDetails(Player player, Match match, String playerType) {
        int actualScoredPoints = player.getScoredPoints();
        int actualLostPoints = player.getLostPoints();

        int playerOneScore = match.getPlayerOneScore();
        int playerTwoScore = match.getPlayerTwoScore();

        switch (playerType) {
            case "playerOne" -> {
                player.setScoredPoints(actualScoredPoints + playerOneScore);
                player.setLostPoints(actualLostPoints + playerTwoScore);
            } case "playerTwo" -> {
                player.setScoredPoints(actualScoredPoints + playerTwoScore);
                player.setLostPoints(actualLostPoints + playerOneScore);
            } default -> {
                throw new IllegalArgumentException("Invalid player type: " + playerType);
            }
        }

        updatePlayerMatches(player, playerOneScore, playerTwoScore, playerType);
        playerRepository.save(player);
    }

    private void updatePlayerMatches(Player player, int pOneScore, int pTwoScore, String playerType) {
        player.setPlayedMatches(player.getPlayedMatches() + 1);
        if (pOneScore == pTwoScore) {
            player.setDraws(player.getDraws() + 1);
        } else {
            boolean playerWins = (pOneScore > pTwoScore && playerType.equals("playerOne")) ||
                (pTwoScore > pOneScore && playerType.equals("playerTwo"));
            if (playerWins) player.setWins(player.getWins() + 1);
            else player.setLosses(player.getLosses() + 1);
        }
    }
}
