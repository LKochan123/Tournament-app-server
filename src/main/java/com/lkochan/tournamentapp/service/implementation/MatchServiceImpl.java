package com.lkochan.tournamentapp.service.implementation;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.lkochan.tournamentapp.entities.Match;
import com.lkochan.tournamentapp.entities.Player;
import com.lkochan.tournamentapp.entities.Tournament;
import com.lkochan.tournamentapp.enums.PlayerPosition;
import com.lkochan.tournamentapp.exception.EntityNotFoundException;
import com.lkochan.tournamentapp.exception.EntityUtils;
import com.lkochan.tournamentapp.exception.MatchNotFoundException;
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

    @Override
    public Match getMatch(Long id) {
        Optional<Match> match = matchRepository.findById(id);
        return EntityUtils.unwrapEntity(match, id, "match");
    }

    @Override
    public void saveMatch(Match match, Long tournamentId, Long playerOneId, Long playerTwoId) {
        boolean isAddingMatch = true;
        Optional<Tournament> tournament = tournamentRepository.findById(tournamentId);
        Optional<Player> playerOne = playerRepository.findById(playerOneId);
        Optional<Player> playerTwo = playerRepository.findById(playerTwoId);

        if (tournament.isPresent() && playerOne.isPresent() && playerTwo.isPresent()) {
            match.setTournament(tournament.get());
            match.setPlayerOne(playerOne.get());
            match.setPlayerTwo(playerTwo.get());

            updatePlayerDetails(playerOne.get(), match, new UpdateDetails(PlayerPosition.PLAYER_ONE, isAddingMatch));
            updatePlayerDetails(playerTwo.get(), match, new UpdateDetails(PlayerPosition.PLAYER_TWO, isAddingMatch));
            matchRepository.save(match);
        } else {
            throw new MatchNotFoundException(tournamentId, playerOneId, playerTwoId);
        }
    }

    @Override
    public void updateMatch(Match match, Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateMatch'");
    }

    @Override
    public void deleteMatch(Long id) {
        boolean isAddingMatch = false;
        Optional<Match> matchOptional = matchRepository.findById(id);

        if (matchOptional.isPresent()) {
            Match match = matchOptional.get();
            Player playerOne = match.getPlayerOne();
            Player playerTwo = match.getPlayerTwo();

            updatePlayerDetails(playerOne, match, new UpdateDetails(PlayerPosition.PLAYER_ONE, isAddingMatch));
            updatePlayerDetails(playerTwo, match, new UpdateDetails(PlayerPosition.PLAYER_TWO, isAddingMatch));
            matchRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException("match", id);
        }
    }

    private void updatePlayerDetails(Player player, Match match, UpdateDetails details) {
        int actualScoredPoints = player.getScoredPoints();
        int actualLostPoints = player.getLostPoints();

        int playerOneScore = match.getPlayerOneScore();
        int playerTwoScore = match.getPlayerTwoScore();

        int playerOneValue = details.isAddingMatch ? playerOneScore : -playerOneScore;
        int playerTwoValue = details.isAddingMatch ? playerTwoScore : -playerTwoScore;

        MatchResult matchResult = new MatchResult(playerOneScore, playerTwoScore);

        switch (details.playerPosition) {
            case PLAYER_ONE -> {
                player.setScoredPoints(actualScoredPoints + playerOneValue);
                player.setLostPoints(actualLostPoints + playerTwoValue);
            } case PLAYER_TWO -> {
                player.setScoredPoints(actualScoredPoints + playerTwoValue);
                player.setLostPoints(actualLostPoints + playerOneValue);
            } default -> {
                throw new IllegalArgumentException("Invalid player type: " + details.playerPosition);
            }
        }

        updatePlayerMatches(player, matchResult, details);
        playerRepository.save(player);
    }

    private void updatePlayerMatches(Player player, MatchResult matchResult, UpdateDetails details) {
        PlayerPosition playerPosition = details.playerPosition;
        int playerOneScore = matchResult.playerOneScore;
        int playerTwoScore = matchResult.playerTwoScore;
        int value = details.isAddingMatch ? 1 : -1;

        player.setPlayedMatches(player.getPlayedMatches() + value);
        
        if (playerOneScore == playerTwoScore) {
            player.setDraws(player.getDraws() + value);
        } else {
            boolean playerOneWins = playerOneScore > playerTwoScore && PlayerPosition.PLAYER_ONE == playerPosition;
            boolean playerTwoWins = playerTwoScore > playerOneScore && PlayerPosition.PLAYER_TWO == playerPosition;
            
            if (playerOneWins || playerTwoWins) player.setWins(player.getWins() + value);
            else player.setLosses(player.getLosses() + value);
        }
    }

    record MatchResult(int playerOneScore, int playerTwoScore) {}
    record UpdateDetails(PlayerPosition playerPosition, boolean isAddingMatch) {}
}
