package com.lkochan.tournamentapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.lkochan.tournamentapp.entities.Tournament;
import com.lkochan.tournamentapp.exception.EntityNotFoundException;
import com.lkochan.tournamentapp.repository.TournamentRepository;
import com.lkochan.tournamentapp.service.implementation.TournamentServiceImpl;

@ExtendWith(MockitoExtension.class)
public class TournamentServiceTest {
    
    @Mock 
    private TournamentRepository tournamentRepository;
    private TournamentServiceImpl tournamentService;
    private Tournament tournament, updatedTournament;
    private Long goodId, wrongId;

    @BeforeEach
    public void setUp() {
        tournamentService = new TournamentServiceImpl(tournamentRepository);
        tournament = new Tournament("SE", "Open", 4);
        updatedTournament = new Tournament("SE", "Closed", 8);
        goodId = tournament.getId();
        wrongId = 123L;
    }

    @Test
    public void getAllTournamentsTest() {
        tournamentService.getAllTournaments();
        verify(tournamentRepository).findAll();
    }

    @Test
    public void getTournamentPositiveTest() {
        when(tournamentRepository.findById(goodId)).thenReturn(Optional.of(tournament));
        
        Tournament result = tournamentService.getTournament(goodId);

        assertEquals(tournament, result);
        verify(tournamentRepository).findById(goodId);
    }

    @Test
    public void getTournamentNegativeTest() {
        when(tournamentRepository.findById(wrongId)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(
            EntityNotFoundException.class, () -> {
            tournamentService.getTournament(wrongId);
        });

        assertTrue(thrown.getMessage().contains("tournament"));
        assertTrue(thrown.getMessage().contains(String.valueOf(wrongId)));
    }

    @Test
    public void saveTournamentTest() {
        tournamentService.saveTournament(tournament);
        verify(tournamentRepository).save(tournament);
    }

    @Test
    public void updateTournamentPositiveTest() {
        when(tournamentRepository.findById(goodId)).thenReturn(Optional.of(tournament));

        tournamentService.updateTournament(goodId, updatedTournament);

        assertEquals(8, tournamentService.getTournament(goodId).getBracketSize());
        assertEquals("Closed", tournamentService.getTournament(goodId).getStatus());
        verify(tournamentRepository).save(tournament);
    }

    @Test
    public void updateTournamentNegativeTest() {
        when(tournamentRepository.findById(wrongId)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(
            EntityNotFoundException.class,
            () -> tournamentService.updateTournament(wrongId, updatedTournament)
        );
    
        assertTrue(thrown.getMessage().contains("tournament"));
        assertTrue(thrown.getMessage().contains(String.valueOf(wrongId)));
    }

    @Test
    public void deleteTournamentPositiveTest() {
        when(tournamentRepository.findById(goodId)).thenReturn(Optional.of(tournament));
        tournamentService.deleteTournament(goodId);
        verify(tournamentRepository).deleteById(goodId);
    }

    @Test
    public void deleteTournamentNegativeTest() {
        when(tournamentRepository.findById(wrongId)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(
            EntityNotFoundException.class,
            () -> tournamentService.deleteTournament(wrongId)
        );

        assertTrue(thrown.getMessage().contains("tournament"));
        assertTrue(thrown.getMessage().contains(String.valueOf(wrongId)));
    }
}
