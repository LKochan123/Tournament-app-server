package com.lkochan.tournamentapp.service.implementation;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lkochan.tournamentapp.entities.Tournament;
import com.lkochan.tournamentapp.exception.EntityNotFoundException;
import com.lkochan.tournamentapp.exception.EntityUtils;
import com.lkochan.tournamentapp.repository.TournamentRepository;
import com.lkochan.tournamentapp.service.interfaces.TournamentService;

@Service
public class TournamentServiceImpl implements TournamentService {

    private static final String message = "tournament";

    @Autowired
    TournamentRepository tournamentRepository;

    @Override
    public List<Tournament> getAllTournaments() {
        return (List<Tournament>) tournamentRepository.findAll();
    }

    @Override
    public Tournament getTournament(Long id) {
        Optional<Tournament> tournament = tournamentRepository.findById(id);
        return EntityUtils.unwrapEntity(tournament, id, message);
    }

    @Override
    public Tournament saveTournament(Tournament tournament) {
        return tournamentRepository.save(tournament);
    }

    @Override
    public void deleteTournament(Long id) {
        Optional<Tournament> tournament = tournamentRepository.findById(id);
        if (tournament.isPresent()) {
            tournamentRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException(message, id);
        }
    }

    // @Override
    // public void updateTournament(Long id) {
    //     // TODO Auto-generated method stub
    //     throw new UnsupportedOperationException("Unimplemented method 'updateTournament'");
    // }
    
}
