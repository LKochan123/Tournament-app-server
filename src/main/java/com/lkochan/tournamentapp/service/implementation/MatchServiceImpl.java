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

            updatePlayerDetails(playerOne.get(), match, "playerOne", isAddingMatch);
            updatePlayerDetails(playerTwo.get(), match, "playerTwo", isAddingMatch);
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
        boolean isAddingMatch = false;
        Optional<Match> matchOptional = matchRepository.findById(id);
        
        if (matchOptional.isPresent()) {
            Match match = matchOptional.get();
            Player playerOne = match.getPlayerOne();
            Player playerTwo = match.getPlayerTwo();

            updatePlayerDetails(playerOne, match, "playerOne", isAddingMatch);
            updatePlayerDetails(playerTwo, match, "playerTwo", isAddingMatch);
            matchRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException("match", id);
        }
    }

    private void updatePlayerDetails(Player player, Match match, String playerType, boolean isAddingMatch) {
        int actualScoredPoints = player.getScoredPoints();
        int actualLostPoints = player.getLostPoints();

        int playerOneScore = match.getPlayerOneScore();
        int playerTwoScore = match.getPlayerTwoScore();

        int playerOneValue = isAddingMatch ? playerOneScore : -playerOneScore;
        int playerTwoValue = isAddingMatch ? playerTwoScore : -playerTwoScore;

        switch (playerType) {
            case "playerOne" -> {
                player.setScoredPoints(actualScoredPoints + playerOneValue);
                player.setLostPoints(actualLostPoints + playerTwoValue);
            } case "playerTwo" -> {
                player.setScoredPoints(actualScoredPoints + playerTwoValue);
                player.setLostPoints(actualLostPoints + playerOneValue);
            } default -> {
                throw new IllegalArgumentException("Invalid player type: " + playerType);
            }
        }

        updatePlayerMatches(player, playerOneScore, playerTwoScore, playerType, isAddingMatch);
        playerRepository.save(player);
    }

    private void updatePlayerMatches(Player player, int pOneScore, int pTwoScore, String playerType, 
    boolean isAddingMatch) {
        int value = isAddingMatch ? 1 : -1;
        player.setPlayedMatches(player.getPlayedMatches() + value);
        if (pOneScore == pTwoScore) {
            player.setDraws(player.getDraws() + value);
        } else {
            boolean playerWins = (pOneScore > pTwoScore && playerType.equals("playerOne")) ||
                (pTwoScore > pOneScore && playerType.equals("playerTwo"));
            if (playerWins) player.setWins(player.getWins() + value);
            else player.setLosses(player.getLosses() + value);
        }
    }
}
