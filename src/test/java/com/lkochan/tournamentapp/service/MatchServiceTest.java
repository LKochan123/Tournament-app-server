package com.lkochan.tournamentapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.lkochan.tournamentapp.entities.Match;
import com.lkochan.tournamentapp.entities.Player;
import com.lkochan.tournamentapp.entities.Tournament;
import com.lkochan.tournamentapp.enums.PlayerPosition;
import com.lkochan.tournamentapp.exception.EntityNotFoundException;
import com.lkochan.tournamentapp.exception.MatchNotFoundException;
import com.lkochan.tournamentapp.repository.MatchRepository;
import com.lkochan.tournamentapp.repository.PlayerRepository;
import com.lkochan.tournamentapp.repository.TournamentRepository;
import com.lkochan.tournamentapp.service.implementation.MatchServiceImpl;
import com.lkochan.tournamentapp.service.implementation.MatchServiceImpl.MatchResult;
import com.lkochan.tournamentapp.service.implementation.MatchServiceImpl.UpdateDetails;

@ExtendWith(MockitoExtension.class)
public class MatchServiceTest {
    
    @Mock private MatchRepository matchRepository;
    @Mock private PlayerRepository playerRepository;
    @Mock private TournamentRepository tournamentRepository;
    private MatchServiceImpl matchService;
    private Tournament tournament;
    private List<Player> players;
    private List<Match> matches;
    private Long playerOneId, playerTwoId, wrongId;

    @BeforeEach
    public void setUp() {
        matchService = new MatchServiceImpl(matchRepository, playerRepository, tournamentRepository);
        tournament = new Tournament("DE", "Open", 8);
        players = createPlayers(tournament);
        matches = createMatches(players);
        playerOneId = players.get(0).getId();
        playerTwoId = players.get(1).getId();
        wrongId = 12345L;
    }

    @AfterEach
    public void tearDown() {
        matchRepository.deleteAll();
        playerRepository.deleteAll();
        tournamentRepository.deleteAll();
    }

    @Test
    public void getAllPlayersMatchesPositiveTest() {
        when(matchRepository.findByPlayerId(playerOneId)).thenReturn(Optional.of(matches));
        matchService.getAllPlayersMatches(playerOneId);
        verify(matchRepository).findByPlayerId(playerOneId);
    }

    @Test
    public void getAllPlayersMatchesNegativeTest() {
        when(matchRepository.findByPlayerId(wrongId)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(
            EntityNotFoundException.class, () -> {
            matchService.getAllPlayersMatches(wrongId);
            }
        );

        assertTrue(thrown.getMessage().contains("player"));
        assertTrue(thrown.getMessage().contains(String.valueOf(wrongId)));
    }

    @Test
    public void getAllTournamentMatchesPositiveTest() {
        Long id = tournament.getId();
        when(matchRepository.findByTournamentId(id)).thenReturn(Optional.of(matches));

        List<Match> result = matchService.getAllTournamentMatches(id);

        assertEquals(matches, result);
        verify(matchRepository).findByTournamentId(id);
    }

    @Test
    public void getAllTournamentMatchesNegativeTest() {
        when(matchRepository.findByTournamentId(wrongId)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(
            EntityNotFoundException.class, () -> {
            matchService.getAllTournamentMatches(wrongId);
            }
        );

        assertTrue(thrown.getMessage().contains("tournament"));
        assertTrue(thrown.getMessage().contains(String.valueOf(wrongId)));
    }

    @Test
    public void getMatchPositiveTest() {
        int idx = 2;
        Match match = matches.get(idx);
        when(matchRepository.findById(match.getId())).thenReturn(Optional.of(match));

        Match result = matchService.getMatch(match.getId());

        assertEquals(match, result);
        verify(matchRepository).findById(match.getId());
    }

    @Test
    public void getMatchNegativeTest() {
        when(matchRepository.findById(wrongId)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(
            EntityNotFoundException.class, () -> {
            matchService.getMatch(wrongId);
            }
        );

        assertTrue(thrown.getMessage().contains("match"));
        assertTrue(thrown.getMessage().contains(String.valueOf(wrongId)));
    }

    @Test
    public void saveMatchPositiveTest() {
        Player player1 = players.get(0), player2 = players.get(1);
        Match match = new Match(player1, 5, player2, 2);
        when(tournamentRepository.findById(tournament.getId())).thenReturn(Optional.of(tournament));
        when(playerRepository.findById(playerOneId)).thenReturn(Optional.of(player1));
        when(playerRepository.findById(playerTwoId)).thenReturn(Optional.of(player2));

        matchService.saveMatch(match, tournament.getId(), player1.getId(), player2.getId());

        verify(matchRepository).save(match);
    }

    @Test
    public void saveMatchNegativeTest() {
        Player player1 = players.get(0), player2 = players.get(1);
        Match match = new Match(player1, 5, player2, 2);
        when(tournamentRepository.findById(wrongId)).thenReturn(Optional.empty());
        when(playerRepository.findById(playerOneId)).thenReturn(Optional.of(player1));
        when(playerRepository.findById(playerTwoId)).thenReturn(Optional.of(player2));

        MatchNotFoundException thrown = assertThrows(
            MatchNotFoundException.class, () -> {
            matchService.saveMatch(match, wrongId, player1.getId(), player2.getId());
            }
        );

        assertTrue(thrown.getMessage().contains(String.valueOf(wrongId)));
        assertTrue(thrown.getMessage().contains(String.valueOf(playerOneId)));
        assertTrue(thrown.getMessage().contains(String.valueOf(playerTwoId)));
    }

    @Test
    public void deleteMatchPositiveTest() {
        Match match = matches.get(0);
        when(matchRepository.findById(match.getId())).thenReturn(Optional.of(match));

        matchService.deleteMatch(match.getId());

        verify(matchRepository).deleteById(match.getId());
    }

    @Test
    public void deleteMatchNegativeTest() {
        when(matchRepository.findById(wrongId)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(
            EntityNotFoundException.class, () -> {
            matchService.getMatch(wrongId);
            }
        );

        assertTrue(thrown.getMessage().contains("match"));
        assertTrue(thrown.getMessage().contains(String.valueOf(wrongId)));
    }

    @Test
    public void updateMatchPositiveTest() {
        int newPlayerOneScore = 1, newPlayerTwoScore = 4;
        Match previousMatch = matches.get(0);
        Long id = previousMatch.getId();
        previousMatch.setPlayerOneScore(newPlayerOneScore);
        previousMatch.setPlayerTwoScore(newPlayerTwoScore);

        when(matchRepository.findById(id)).thenReturn(Optional.of(previousMatch));

        matchService.updateMatch(previousMatch, id);

        assertEquals(newPlayerOneScore, matchService.getMatch(id).getPlayerOneScore());
        assertEquals(newPlayerTwoScore, matchService.getMatch(id).getPlayerTwoScore());
        verify(matchRepository).save(previousMatch);
    }

    @Test
    public void updateMatchNegativeTest() {
        int newPlayerOneScore = 1, newPlayerTwoScore = 4;
        Match previousMatch = matches.get(0);
        previousMatch.setPlayerOneScore(newPlayerOneScore);
        previousMatch.setPlayerTwoScore(newPlayerTwoScore);

        when(matchRepository.findById(wrongId)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(
            EntityNotFoundException.class, () -> {
            matchService.updateMatch(previousMatch, wrongId);
            }
        );

        assertTrue(thrown.getMessage().contains("match"));
        assertTrue(thrown.getMessage().contains(String.valueOf(wrongId)));
    }

    @Test
    public void updatePlayerPointsTest() {
        int playerOneScore = 4, playerTwoScore = 3;
        Player playerOne = players.get(0), playerTwo = players.get(1);
        Match match = new Match(playerOne, playerOneScore, playerTwo, playerTwoScore);

        matchService.updatePlayerPoints(playerOne, match, new UpdateDetails(PlayerPosition.PLAYER_ONE, true));
        matchService.updatePlayerPoints(playerTwo, match, new UpdateDetails(PlayerPosition.PLAYER_TWO, true));

        assertEquals(playerOneScore, playerOne.getScoredPoints());
        assertEquals(playerTwoScore, playerOne.getLostPoints());
        assertEquals(playerTwoScore, playerTwo.getScoredPoints());
        assertEquals(playerOneScore, playerTwo.getLostPoints());
    }

    @Test
    public void updatePlayerMatchesTest() {
        int playerOneScore = 4, playerTwoScore = 7;
        Player playerOne = players.get(0), playerTwo = players.get(1);
        MatchResult result = new MatchResult(playerOneScore, playerTwoScore);

        matchService.updatePlayerMatches(playerOne, result, new UpdateDetails(PlayerPosition.PLAYER_ONE, true));
        matchService.updatePlayerMatches(playerTwo, result, new UpdateDetails(PlayerPosition.PLAYER_TWO, true));

        assertEquals(1, playerOne.getLosses());
        assertEquals(0, playerOne.getWins());
        assertEquals(0, playerTwo.getLosses());
        assertEquals(1, playerTwo.getWins());
    }

    private List<Player> createPlayers(Tournament tournament) {
        Player player1 = new Player("Masuj", 1, 0, 0, 0, 0, 0, 0, tournament);
        Player player2 = new Player("Misiek", 2, 0, 0, 0, 0, 0, 0, tournament);
        Player player3 = new Player("Bearer", 3, 0, 0, 0, 0, 0, 0, tournament);
        Player player4 = new Player("Cowy", 4, 2, 0, 0, 0, 0, 0, tournament);
        return Arrays.asList(player1, player2, player3, player4);
    }

    private List<Match> createMatches(List<Player> players) {
        Match match1 = new Match(players.get(0), 3, players.get(1), 2);
        Match match2 = new Match(players.get(1), 2, players.get(3), 4);
        Match match3 = new Match(players.get(3), 1, players.get(2), 1);
        Match match4 = new Match(players.get(1), 5, players.get(0), 5);
        return Arrays.asList(match1, match2, match3, match4);
    }

}
