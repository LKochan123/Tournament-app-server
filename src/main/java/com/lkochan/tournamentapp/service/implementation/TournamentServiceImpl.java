package com.lkochan.tournamentapp.service.implementation;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.lkochan.tournamentapp.entities.Tournament;
import com.lkochan.tournamentapp.exception.EntityNotFoundException;
import com.lkochan.tournamentapp.exception.EntityUtils;
import com.lkochan.tournamentapp.repository.TournamentRepository;
import com.lkochan.tournamentapp.service.interfaces.TournamentService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class TournamentServiceImpl implements TournamentService {

    TournamentRepository tournamentRepository;

    @Override
    public List<Tournament> getAllTournaments() {
        return (List<Tournament>) tournamentRepository.findAll();
    }

    @Override
    public Tournament getTournament(Long id) {
        Optional<Tournament> tournament = tournamentRepository.findById(id);
        return EntityUtils.unwrapEntity(tournament, id, "tournament");
    }

    @Override
    public Tournament saveTournament(Tournament tournament) {
        return tournamentRepository.save(tournament);
    }

    @Override
    public void updateTournament(Long id, Tournament tournamentDetails) {
        Tournament tournament = EntityUtils.unwrapEntity(tournamentRepository.findById(id), id, "tournament");
        BeanUtils.copyProperties(tournamentDetails, tournament, "id");
        tournamentRepository.save(tournament);
    }

    @Override
    public void deleteTournament(Long id) {
        Optional<Tournament> tournament = tournamentRepository.findById(id);
        if (tournament.isPresent()) {
            tournamentRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException("tournament", id);
        }
    }
}
