package com.lkochan.tournamentapp.service.implementation;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.lkochan.tournamentapp.entities.Player;
import com.lkochan.tournamentapp.entities.Tournament;
import com.lkochan.tournamentapp.exception.EntityNotFoundException;
import com.lkochan.tournamentapp.exception.EntityUtils;
import com.lkochan.tournamentapp.repository.PlayerRepository;
import com.lkochan.tournamentapp.repository.TournamentRepository;
import com.lkochan.tournamentapp.service.interfaces.PlayerService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class PlayerServiceImpl implements PlayerService {

    PlayerRepository playerRepository;
    TournamentRepository tournamentRepository;
    private static final String message = "player";

    @Override
    public List<Player> getPlayers(Long tournamentId) {
        Optional<List<Player>> players = playerRepository.findByTournamentId(tournamentId);
        return EntityUtils.unwrapEntity(players, tournamentId, "tournament");
    }

    @Override
    public Player getPlayer(Long id) {
        Optional<Player> player = playerRepository.findById(id);
        return EntityUtils.unwrapEntity(player, id, message);
    }

    @Override
    public void savePlayer(Player player, Long tournamentId) {
        Optional<Tournament> tournament = tournamentRepository.findById(tournamentId);
        if (tournament.isPresent()) {
            player.setTournament(tournament.get());
            playerRepository.save(player);
        } else {
            throw new EntityNotFoundException("tournament", tournamentId);
        }
    }

    @Override
    public void updatePlayer(Long id, Player player) {
        Player p = EntityUtils.unwrapEntity(playerRepository.findById(id), id, message);
        p.setLosses(player.getLosses());
        p.setPlayedMatches(player.getPlayedMatches());
        p.setSeeding(player.getSeeding());
        p.setUsername(player.getUsername());
        p.setWins(player.getWins());
        playerRepository.save(p);
    }

    @Override
    public void deletePlayer(Long id) {
        Optional<Player> player = playerRepository.findById(id);
        if (player.isPresent()) {
            playerRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException(message, id);
        }
    }
    
}
