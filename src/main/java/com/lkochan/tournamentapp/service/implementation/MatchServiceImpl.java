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

            updatePlayerStats(playerOne.get(), match, new UpdateDetails(PlayerPosition.PLAYER_ONE, isAddingMatch));
            updatePlayerStats(playerTwo.get(), match, new UpdateDetails(PlayerPosition.PLAYER_TWO, isAddingMatch));
            matchRepository.save(match);
        } else {
            throw new MatchNotFoundException(tournamentId, playerOneId, playerTwoId);
        }
    }

    @Override
    public void deleteMatch(Long id) {
        boolean isAddingMatch = false;
        Optional<Match> matchOptional = matchRepository.findById(id);

        if (matchOptional.isPresent()) {
            Match match = matchOptional.get();
            Player playerOne = match.getPlayerOne();
            Player playerTwo = match.getPlayerTwo();

            updatePlayerStats(playerOne, match, new UpdateDetails(PlayerPosition.PLAYER_ONE, isAddingMatch));
            updatePlayerStats(playerTwo, match, new UpdateDetails(PlayerPosition.PLAYER_TWO, isAddingMatch));
            matchRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException("match", id);
        }
    }

    @Override
    public void updateMatch(Match matchDetails, Long id) {
        Optional<Match> matchOptional = matchRepository.findById(id);

        if (matchOptional.isPresent()) {
            Match match = matchOptional.get();

            int playerOneLastScore = match.getPlayerOneScore();
            int playerTwoLastScore = match.getPlayerTwoScore();
            int playerOneCurrScore = matchDetails.getPlayerOneScore();
            int playerTwoCurrScore = matchDetails.getPlayerTwoScore();
            Player playerOne = match.getPlayerOne();
            Player playerTwo = match.getPlayerTwo();

            if (isResultChanged(playerOneLastScore, playerTwoLastScore, playerOneCurrScore, playerTwoCurrScore)) {
                updatePlayerStats(playerOne, match, new UpdateDetails(PlayerPosition.PLAYER_ONE, false));
                updatePlayerStats(playerTwo, match, new UpdateDetails(PlayerPosition.PLAYER_TWO, false));
                updatePlayerStats(playerOne, matchDetails, new UpdateDetails(PlayerPosition.PLAYER_ONE, true));
                updatePlayerStats(playerTwo, matchDetails, new UpdateDetails(PlayerPosition.PLAYER_TWO, true));

                match.setPlayerOneScore(playerOneCurrScore);
                match.setPlayerTwoScore(playerTwoCurrScore);
            }
            matchRepository.save(match);
        } else {
            throw new EntityNotFoundException("match", id);
        }
    }

    public void updatePlayerPoints(Player player, Match match, UpdateDetails details) {
        int actualScoredPoints = player.getScoredPoints();
        int actualLostPoints = player.getLostPoints();

        int playerOneScore = match.getPlayerOneScore();
        int playerTwoScore = match.getPlayerTwoScore();

        int playerOneValue = details.isAddingMatch ? playerOneScore : -1 * playerOneScore;
        int playerTwoValue = details.isAddingMatch ? playerTwoScore : -1 * playerTwoScore;

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
    }

    public void updatePlayerMatches(Player player, MatchResult matchResult, UpdateDetails details) {
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

    private void updatePlayerStats(Player player, Match match, UpdateDetails details) {
        MatchResult matchResult = new MatchResult(match.getPlayerOneScore(), match.getPlayerTwoScore());
        updatePlayerPoints(player, match, details);
        updatePlayerMatches(player, matchResult, details);
        playerRepository.save(player);
    }

    private boolean isResultChanged(int pOneLastScore, int pTwoLastScore, int pOneCurrScore, int pTwoCurrScore) {
        if ((pOneLastScore != pOneCurrScore) || (pTwoLastScore != pTwoCurrScore)) return true;
        else return false;
    }

    public record MatchResult(int playerOneScore, int playerTwoScore) {}
    public record UpdateDetails(PlayerPosition playerPosition, boolean isAddingMatch) {}
}
