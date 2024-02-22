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
import com.lkochan.tournamentapp.entities.Player;
import com.lkochan.tournamentapp.entities.Tournament;
import com.lkochan.tournamentapp.exception.EntityNotFoundException;
import com.lkochan.tournamentapp.service.interfaces.PlayerService;
import com.lkochan.tournamentapp.service.interfaces.TournamentService;

@WebMvcTest(PlayerController.class)
@AutoConfigureMockMvc()
public class PlayerControllerIntegrationTest {
    
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private PlayerService playerService;
    @MockBean private TournamentService tournamentService;
    private List<Player> players;
    private Tournament tournament;
    private Long invalidID = 12345L;
    private EntityNotFoundException exception = new EntityNotFoundException("Player", invalidID);

    @BeforeEach
    public void setUp() {
        tournament = new Tournament(1L, "SE", "Open", 8);
        players = createPlayers(tournament);
        tournamentService.saveTournament(tournament);
        players.forEach(player -> playerService.savePlayer(player, tournament.getId()));
    }

    @AfterEach
    public void tearDown() {
        playerService.getPlayers(tournament.getId()).clear();
        tournamentService.getAllTournaments().clear();
    }

    @Test
    public void getAllPlayersTest() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.get("/player/all/{id}", tournament.getId());

        mockMvc.perform(request)
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void getAllInvalidPlayers() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.get("/player/all/{id}", invalidID);
        when(playerService.getPlayers(invalidID)).thenThrow(exception);
        mockMvc.perform(request).andExpect(status().isNotFound());
    }

    @Test
    public void getPlayerTest() throws Exception {
        Long id = 1L;
        RequestBuilder request = MockMvcRequestBuilders.get("/player/{id}", id);
        when(playerService.getPlayer(id)).thenReturn(players.get(0));

        mockMvc.perform(request)
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.username").value("Masuj"))
            .andExpect(jsonPath("$.draws").value(1));
    }

    @Test
    public void getInvalidPlayerTest() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.get("/player/{id}", invalidID);
        when(playerService.getPlayer(invalidID)).thenThrow(exception);
        mockMvc.perform(request).andExpect(status().isNotFound());
    }

    @Test
    public void addPlayerTest() throws Exception {
        Player player = new Player(1L, "Yoze", 1, 2, 1, 1, 0, 8, 3, tournament);
        RequestBuilder request = MockMvcRequestBuilders.post("/player/{tournamentId}", tournament.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(player));

        mockMvc.perform(request).andExpect(status().isCreated());
    }

    @Test 
    public void addInvalidPlayerTest() throws Exception {
        Player player = new Player(1L, "Yoze", 5, 2, -1, 1, 0, 8, 3, tournament);
        RequestBuilder request = MockMvcRequestBuilders.post("/player/{tournamentId}", tournament.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(player));

        mockMvc.perform(request).andExpect(status().isBadRequest());
    }

    @Test
    public void deletePlayerTest() throws Exception {
        Long id = 1L;
        RequestBuilder request = MockMvcRequestBuilders.delete("/player/{id}", id);
        doNothing().when(tournamentService).deleteTournament(id);
        mockMvc.perform(request).andExpect(status().isNoContent());
    }

    @Test
    public void deleteInvalidPlayerTest() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.delete("/player/{id}", invalidID);
        doThrow(exception).when(playerService).deletePlayer(invalidID);
        mockMvc.perform(request).andExpect(status().isNotFound());
    }

    @Test
    public void updatePlayerTest() throws Exception {
        Player player = players.get(2);
        player.setUsername("Johny");

        RequestBuilder request = MockMvcRequestBuilders.put("/player/{id}", 3L)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(player));

        mockMvc.perform(request).andExpect(status().isOk());
    }

    @Test
    public void updateInvalidPlayerTest() throws Exception {
        Player player = players.get(2);
        player.setSeeding(256);

        RequestBuilder request = MockMvcRequestBuilders.put("/player/{id}", 3L)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(player));

        mockMvc.perform(request).andExpect(status().isBadRequest());
    }

    private List<Player> createPlayers(Tournament tournament) {
        return Arrays.asList(
            new Player(1L, "Masuj", 1, 2, 1, 1, 0, 8, 3, tournament),
            new Player(2L, "Misiek", 2, 2, 0, 1, 1, 5, 2, tournament),
            new Player(3L, "Bearer", 3, 2, 0, 1, 1, 7, 3, tournament),
            new Player(4L, "Gimmy", 4, 2, 0, 1, 1, 5, 4, tournament)
        );
    }

}
