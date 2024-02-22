package com.lkochan.tournamentapp.controllers;

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

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lkochan.tournamentapp.entities.Tournament;
import com.lkochan.tournamentapp.exception.EntityNotFoundException;
import com.lkochan.tournamentapp.service.interfaces.TournamentService;

@WebMvcTest(TournamentController.class)
@AutoConfigureMockMvc()
public class TournamentControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private TournamentService tournamentService;
    private List<Tournament> tournaments;
    private Long id, invalidID;
    private EntityNotFoundException exception;

    @BeforeEach
    public void setUp() {
        tournaments = createTournaments();
        tournaments.forEach(tournament -> tournamentService.saveTournament(tournament));
        id = tournaments.get(0).getId();
        invalidID = 12345L;
        exception = new EntityNotFoundException("Tournament", invalidID);
    }

    @AfterEach
    public void tearDown() {
        tournamentService.getAllTournaments().clear();
    }

    @Test
    public void getAllTournamentsTest() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.get("/tournament/all");

        mockMvc.perform(request)
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void getTournamentTest() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.get("/tournament/{id}", id);
        when(tournamentService.getTournament(id)).thenReturn(tournaments.get(0));

        mockMvc.perform(request)
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.type").value("SE"))
            .andExpect(jsonPath("$.bracketSize").value(4));
    }

    @Test
    public void getInvalidTournamentTest() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.get("/tournament/{id}", invalidID);
        when(tournamentService.getTournament(invalidID)).thenThrow(exception);
        mockMvc.perform(request).andExpect(status().isNotFound());
    }

    @Test
    public void addValidTournamentTest() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.post("/tournament")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new Tournament("DE", "Open", 16)));

        mockMvc.perform(request).andExpect(status().isCreated());
    }

    @Test
    public void addInvalidTournamentTest() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.post("/tournament")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new Tournament("SE", " ", 32)));

        mockMvc.perform(request).andExpect(status().isBadRequest());
    }

    @Test
    public void updateTournamentTest() throws Exception {
        Tournament tournament = tournaments.get(1);
        tournament.setStatus("Closed");

        RequestBuilder request = MockMvcRequestBuilders.put("/tournament/{id}", tournament.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(tournament));

        mockMvc.perform(request).andExpect(status().isOk());
    }

    @Test
    public void updateInvalidTournament() throws Exception {
        Tournament tournament = tournaments.get(1);
        tournament.setStatus("   ");

        RequestBuilder request = MockMvcRequestBuilders.put("/tournament/{id}", tournament.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(tournament));

        mockMvc.perform(request).andExpect(status().isBadRequest());
    }

    @Test
    public void deleteTournamentTest() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.delete("/tournament/{id}", id);
        doNothing().when(tournamentService).deleteTournament(id);
        mockMvc.perform(request).andExpect(status().isNoContent());
    }

    @Test
    public void deleteInvalidTournament() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.delete("/tournament/{id}", invalidID);
        doThrow(exception).when(tournamentService).deleteTournament(invalidID);
        mockMvc.perform(request).andExpect(status().isNotFound());
    }

    private List<Tournament> createTournaments() {
        return Arrays.asList(
            new Tournament(1L, "SE", "Open", 4),
            new Tournament(2L, "DE", "League format", 8)
        );
    }

}
