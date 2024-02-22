package com.lkochan.tournamentapp.controllers;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lkochan.tournamentapp.entities.Match;
import com.lkochan.tournamentapp.entities.Player;
import com.lkochan.tournamentapp.entities.Tournament;
import com.lkochan.tournamentapp.exception.EntityNotFoundException;
import com.lkochan.tournamentapp.service.interfaces.MatchService;
import com.lkochan.tournamentapp.service.interfaces.PlayerService;
import com.lkochan.tournamentapp.service.interfaces.TournamentService;

@WebMvcTest(MatchController.class)
@AutoConfigureMockMvc()
public class MatchControllerIntegrationTest {
    
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private MatchService matchService;
    @MockBean private PlayerService playerService;
    @MockBean private TournamentService tournamentService;
    private List<Match> matches;
    private List<Player> players;
    private Tournament tournament;
    private Long invalidID = 12345L;
    private EntityNotFoundException exception = new EntityNotFoundException("match", invalidID);

    @BeforeEach
    public void setUp() {
        tournament = new Tournament(1L, "SE", "Open", 8);
        players = createPlayers(tournament);
        matches = createMatches(players);

        tournamentService.saveTournament(tournament);
        players.forEach(player -> playerService.savePlayer(player, tournament.getId()));
        matches.forEach(match -> matchService.saveMatch(
            match, tournament.getId(), match.getPlayerOne().getId(), match.getPlayerTwo().getId())
        );
    }

    @AfterEach
    public void tearDown() {
        matchService.getAllTournamentMatches(tournament.getId()).clear();
        playerService.getPlayers(tournament.getId()).clear();
        tournamentService.getAllTournaments().clear();
    }

    @Test
    public void getPlayerMatchesTest() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.get("/match/player/{playerId}", 1L);

        mockMvc.perform(request)
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void getPlayersMatchesInvalidTest() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.get("/match/player/{playerId}", invalidID);
        when(matchService.getAllPlayersMatches(invalidID)).thenThrow(exception);
        mockMvc.perform(request).andExpect(status().isNotFound());
    }

    @Test
    public void getTournamentMatchesTest() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.get("/match/tournament/{tournamentId}", 1L);

        mockMvc.perform(request)
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void getTournamentMatchesInvalidTest() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.get("/match/tournament/{tournamentId}", invalidID);
        when(matchService.getAllTournamentMatches(invalidID)).thenThrow(exception);
        mockMvc.perform(request).andExpect(status().isNotFound());
    }

    @Test
    public void getMatchTest() throws Exception {
        Long id = 2L;
        RequestBuilder request = MockMvcRequestBuilders.get("/match/{id}", id);
        when(matchService.getMatch(id)).thenReturn(matches.get(1));

        mockMvc.perform(request)
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath( "$.playerOneScore").value(2))
            .andExpect(jsonPath("$.playerTwoScore").value(4));
    }

    @Test
    public void getMatchInvalidTest() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.get("/match/{id}", invalidID);
        when(matchService.getMatch(invalidID)).thenThrow(exception);
        mockMvc.perform(request).andExpect(status().isNotFound());
    }

    @Test
    public void createMatchTest() throws Exception {
        Match match = new Match(5L, players.get(2), 0, players.get(0), 3);
        RequestBuilder request = MockMvcRequestBuilders.post(
            "/match/add/{tournamentId}/{playerOneId}/{playerTwoId}", 
            tournament.getId(), 
            match.getPlayerOne().getId(), 
            match.getPlayerTwo().getId()
            )
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(match));

        mockMvc.perform(request).andExpect(status().isCreated());
    }

    @Test
    public void createMatchInvalidTest() throws Exception {
        Match invalidMatch = new Match(6L, players.get(2), -2, players.get(0), -4);
        RequestBuilder request = MockMvcRequestBuilders.post(
            "/match/add/{tournamentId}/{playerOneId}/{playerTwoId}", 
            tournament.getId(), 
            invalidMatch.getPlayerOne().getId(), 
            invalidMatch.getPlayerTwo().getId()
            )
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidMatch));

        mockMvc.perform(request).andExpect(status().isBadRequest());
    }

    @Test 
    public void deleteMatchTest() throws Exception {
        Long id = 1L;
        RequestBuilder request = MockMvcRequestBuilders.delete("/match/{id}", id);
        doNothing().when(matchService).deleteMatch(id);
        mockMvc.perform(request).andExpect(status().isNoContent());
    }

    @Test
    public void deleteMatchInvalidTest() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.delete("/match/{id}", invalidID);
        doThrow(exception).when(matchService).deleteMatch(invalidID);
        mockMvc.perform(request).andExpect(status().isNotFound());
    }

    @Test
    public void updateMatchTest() throws Exception {
        Match match = matches.get(0);
        match.setPlayerOneScore(7);

        RequestBuilder request = MockMvcRequestBuilders.put("/match/{id}", match.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(match));

        mockMvc.perform(request).andExpect(status().isOk());
    }

    @Test
    public void updateMatchInvalidTest() throws Exception {
        Match match = matches.get(0);
        match.setPlayerTwoScore(-3);

        RequestBuilder request = MockMvcRequestBuilders.put("/match/{id}", match.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(match));

        mockMvc.perform(request).andExpect(status().isBadRequest());
    }

    private List<Player> createPlayers(Tournament tournament) {
        return Arrays.asList(
            new Player(1L, "Masuj", 1, 0, 0, 0, 0, 0, 0, tournament),
            new Player(2L, "Misiek", 2, 0, 0, 0, 0, 0, 0, tournament),
            new Player(3L, "Bearer", 3, 0, 0, 0, 0, 0, 0, tournament),
            new Player(4L, "Cowy", 4, 2, 0, 0, 0, 0, 0, tournament)
        );
    }

    private List<Match> createMatches(List<Player> players) {
        return Arrays.asList(
            new Match(1L, players.get(0), 3, players.get(1), 2),
            new Match(2L, players.get(1), 2, players.get(3), 4),
            new Match(3L, players.get(3), 1, players.get(2), 1),
            new Match(4L, players.get(1), 5, players.get(0), 5)
        );
    }

}
