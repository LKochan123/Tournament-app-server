package com.lkochan.tournamentapp.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lkochan.tournamentapp.entities.Tournament;
import com.lkochan.tournamentapp.exception.EntityNotFoundException;
import com.lkochan.tournamentapp.service.interfaces.TournamentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/tournament")
public class TournamentController {
    
    @Autowired
    TournamentService tournamentService;

    @GetMapping("/all")
    public ResponseEntity<List<Tournament>> getAllTournaments() {
        List<Tournament> tournaments = tournamentService.getAllTournaments();
        return new ResponseEntity<>(tournaments, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tournament> getTournament(@PathVariable Long id) throws EntityNotFoundException {
        Tournament tournament = tournamentService.getTournament(id);
        return new ResponseEntity<>(tournament, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Tournament> addTournament(@RequestBody @Valid Tournament tournament) {
        Tournament createdTournament = tournamentService.saveTournament(tournament);
        return new ResponseEntity<>(createdTournament, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HttpStatus> updateTournament(@RequestBody @Valid Tournament tournament, @PathVariable Long id) throws EntityNotFoundException {
        tournamentService.updateTournament(id, tournament);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delteTournament(@PathVariable Long id) throws EntityNotFoundException {
        tournamentService.deleteTournament(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
