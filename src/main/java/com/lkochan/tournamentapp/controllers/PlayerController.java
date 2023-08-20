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

import com.lkochan.tournamentapp.entities.Player;
import com.lkochan.tournamentapp.exception.PlayerNotFoundException;
import com.lkochan.tournamentapp.exception.TournamentNotFoundException;
import com.lkochan.tournamentapp.service.interfaces.PlayerService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/player")
public class PlayerController {
    
    @Autowired
    PlayerService playerService;

    @GetMapping("/all/{tournament_id}")
    public ResponseEntity<List<Player>> getPlayerse(@PathVariable Long tournament_id) {
        List<Player> players = playerService.getPlayers(tournament_id);
        return new ResponseEntity<>(players, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Player> getPlayer(@PathVariable Long id) throws PlayerNotFoundException {
        Player player = playerService.getPlayer(id);
        return new ResponseEntity<>(player, HttpStatus.OK);
    }

    @PostMapping("/{tournament_id}")
    public ResponseEntity<HttpStatus> addPlayer(@RequestBody @Valid Player player, @PathVariable Long tournament_id) throws TournamentNotFoundException {
        playerService.savePlayer(player, tournament_id);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HttpStatus> updatePlayer(@RequestBody @Valid Player player, @PathVariable Long id) throws PlayerNotFoundException {
        playerService.updatePlayer(id, player);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deletePlayer(@PathVariable Long id) throws PlayerNotFoundException {
        playerService.deletePlayer(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
