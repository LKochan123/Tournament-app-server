package com.lkochan.tournamentapp.service.implementation;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lkochan.tournamentapp.entities.Tournament;
import com.lkochan.tournamentapp.exception.TournamentNotFoundException;
import com.lkochan.tournamentapp.repository.TournamentRepository;
import com.lkochan.tournamentapp.service.interfaces.TournamentService;

@Service
public class TournamentServiceImpl implements TournamentService {

    @Autowired
    TournamentRepository tournamentRepository;

    @Override
    public List<Tournament> getAllTournaments() {
        return (List<Tournament>) tournamentRepository.findAll();
    }

    @Override
    public Tournament getTournament(Long id) {
        Optional<Tournament> tournament = tournamentRepository.findById(id);
        return unwrapTournament(tournament, id);
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
            throw new TournamentNotFoundException(id);
        }
    }

    // @Override
    // public void updateTournament(Long id) {
    //     // TODO Auto-generated method stub
    //     throw new UnsupportedOperationException("Unimplemented method 'updateTournament'");
    // }

    static Tournament unwrapTournament(Optional<Tournament> entity, Long id) {
        if (entity.isPresent()) return entity.get();
        else throw new TournamentNotFoundException(id);
    }
    
}
