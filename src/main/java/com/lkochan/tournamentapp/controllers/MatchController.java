package com.lkochan.tournamentapp.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lkochan.tournamentapp.entities.Match;
import com.lkochan.tournamentapp.exception.EntityNotFoundException;
import com.lkochan.tournamentapp.service.interfaces.MatchService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/match")
public class MatchController {
    
    @Autowired
    MatchService matchService;

    @GetMapping("/player/{playerId}")
    public ResponseEntity<List<Match>> getPlayerMatches(@PathVariable Long playerId) throws EntityNotFoundException {
        List<Match> matches = matchService.getAllPlayersMatches(playerId);
        return new ResponseEntity<>(matches, HttpStatus.OK);
    }

    @GetMapping("/tournament/{tournamentId}")
        public ResponseEntity<List<Match>> getTournamentMatches(@PathVariable Long tournamentId) throws EntityNotFoundException {
        List<Match> matches = matchService.getAllTournamentMatches(tournamentId);
        return new ResponseEntity<>(matches, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Match> getMatch(@PathVariable Long id) throws EntityNotFoundException {
        Match match = matchService.getMatch(id);
        return new ResponseEntity<>(match, HttpStatus.OK);
    }

    @PostMapping("/add/{tournamentId}/{playerOneId}/{playerTwoId}")
    public ResponseEntity<HttpStatus> createMatch(@RequestBody @Valid Match match, @PathVariable Long tournamentId, @PathVariable Long playerOneId, @PathVariable Long playerTwoId) throws EntityNotFoundException {
        matchService.saveMatch(match, tournamentId, playerOneId, playerTwoId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteMatch(@PathVariable Long id) throws EntityNotFoundException {
        matchService.deleteMatch(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
