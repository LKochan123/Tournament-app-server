package com.lkochan.tournamentapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.lkochan.tournamentapp.entities.Player;
import com.lkochan.tournamentapp.entities.Tournament;
import com.lkochan.tournamentapp.exception.EntityNotFoundException;
import com.lkochan.tournamentapp.repository.PlayerRepository;
import com.lkochan.tournamentapp.repository.TournamentRepository;
import com.lkochan.tournamentapp.service.implementation.PlayerServiceImpl;

@ExtendWith(MockitoExtension.class)
public class PlayerServiceTest {
    
    @Mock private PlayerRepository playerRepository;
    @Mock private TournamentRepository tournamentRepository;
    private PlayerServiceImpl playerService;
    private Tournament tournament;
    private List<Player> players;
    private Long tournamentGoodId, playerOneId, wrongId;

    @BeforeEach
    public void setUp() {
        playerService = new PlayerServiceImpl(playerRepository, tournamentRepository);
        tournament = new Tournament("DE", "Open", 8);
        players = createPlayers(tournament);
        tournamentGoodId = tournament.getId();
        playerOneId = players.get(0).getId();
        wrongId = 12345L;
    }

    @Test
    public void getPlayersPositiveTest() {
        when(playerRepository.findByTournamentIdAndOrderByPoints(tournamentGoodId)).thenReturn(Optional.of(players));

        List<Player> result = playerService.getPlayers(tournamentGoodId);

        assertEquals(players, result);
        verify(playerRepository).findByTournamentIdAndOrderByPoints(tournamentGoodId);
    }

    @Test
    public void getPlayersNegativeTest() {
        when(playerRepository.findByTournamentIdAndOrderByPoints(wrongId)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(
            EntityNotFoundException.class, () -> {
            playerService.getPlayers(wrongId);
        });

        assertTrue(thrown.getMessage().contains("tournament"));
        assertTrue(thrown.getMessage().contains(String.valueOf(wrongId)));
    }

    @Test
    public void getPlayerPositiveTest() {
        when(playerRepository.findById(playerOneId)).thenReturn(Optional.of(players.get(0)));

        Player result = playerService.getPlayer(playerOneId);

        assertEquals(players.get(0), result);
        verify(playerRepository).findById(playerOneId);
    }

    @Test
    public void getPlayerNegativeTest() {
        when(playerRepository.findById(wrongId)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(
            EntityNotFoundException.class, () -> {
            playerService.getPlayer(wrongId);
        });

        assertTrue(thrown.getMessage().contains("player"));
        assertTrue(thrown.getMessage().contains(String.valueOf(wrongId)));
    }

    @Test
    public void savePlayerPositiveTest() {
        Player newPlayer = createNewPlayer(tournament);
        when(tournamentRepository.findById(tournamentGoodId)).thenReturn(Optional.of(tournament));

        playerService.savePlayer(newPlayer, tournamentGoodId);
        verify(playerRepository).save(newPlayer);
    }

    @Test
    public void savePlayerNegativeTest() {
        Player newPlayer = createNewPlayer(tournament);
        when(tournamentRepository.findById(wrongId)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(
            EntityNotFoundException.class, () -> {
            playerService.savePlayer(newPlayer, wrongId);;
        });

        assertTrue(thrown.getMessage().contains("tournament"));
        assertTrue(thrown.getMessage().contains(String.valueOf(wrongId)));
    }

    @Test
    public void updatePlayerPositiveTest() {
        String newUsername = "Gimmy";
        int newSeeding = 6;

        Player beforeUpdatePlayer = players.get(0);
        Player updatedPlayer = updatePlayer(0, newUsername, newSeeding);
        when(playerRepository.findById(playerOneId)).thenReturn(Optional.of(beforeUpdatePlayer));

        playerService.updatePlayer(playerOneId, updatedPlayer);

        assertEquals(newUsername, playerService.getPlayer(playerOneId).getUsername());
        assertEquals(newSeeding, playerService.getPlayer(playerOneId).getSeeding());
        verify(playerRepository).save(beforeUpdatePlayer);
    }

    @Test
    public void updatePlayerNegativeTest() {
        Player updatedPlayer = updatePlayer(0, "Brosky", 7);
        when(playerRepository.findById(wrongId)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(
            EntityNotFoundException.class, () -> {
            playerService.updatePlayer(wrongId, updatedPlayer);
        });

        assertTrue(thrown.getMessage().contains("player"));
        assertTrue(thrown.getMessage().contains(String.valueOf(wrongId)));
    }

    @Test
    public void deletePlayerPositiveTest() {
        when(playerRepository.findById(playerOneId)).thenReturn(Optional.of(players.get(0)));
        playerService.deletePlayer(playerOneId);
        verify(playerRepository).deleteById(playerOneId);
    }

    @Test
    public void deletePlayerNegativeTest() {
        when(playerRepository.findById(wrongId)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(
            EntityNotFoundException.class,() -> {
            playerService.deletePlayer(wrongId);
        });

        assertTrue(thrown.getMessage().contains("player"));
        assertTrue(thrown.getMessage().contains(String.valueOf(wrongId)));
    }

    private List<Player> createPlayers(Tournament tournament) {
        return Arrays.asList(
            new Player("Masuj", 1, 2, 1, 1, 0, 8, 3, tournament),
            new Player("Misiek", 2, 2, 0, 1, 1, 5, 2, tournament),
            new Player("Bearer", 3, 2, 0, 1, 1, 7, 3, tournament),
            new Player("", 4, 2, 0, 1, 1, 5, 4, tournament)
        );
    }

    private Player createNewPlayer(Tournament tournament) {
        return new Player("Johny", 5, 0, 0, 0, 0, 0, 0, tournament); 
    }

    private Player updatePlayer(int idx, String username, int seeding) {
        Player player = players.get(0);
        player.setUsername(username);
        player.setSeeding(seeding);
        return player;
    }

}
