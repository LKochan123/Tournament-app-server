package com.lkochan.tournamentapp.service.interfaces;

import java.util.List;

import com.lkochan.tournamentapp.entities.Tournament;

public interface TournamentService {
    List<Tournament> getAllTournaments();
    Tournament getTournament(Long id);
    Tournament saveTournament(Tournament tournament);
    void deleteTournament(Long id);
    // void updateTournament(Long id);
}
