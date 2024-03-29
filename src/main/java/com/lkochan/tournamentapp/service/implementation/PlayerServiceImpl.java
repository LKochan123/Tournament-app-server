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

    @Override
    public List<Player> getPlayers(Long tournamentId) {
        Optional<List<Player>> players = playerRepository.findByTournamentIdAndOrderByPoints(tournamentId);
        return EntityUtils.unwrapEntity(players, tournamentId, "tournament");
    }

    @Override
    public Player getPlayer(Long id) {
        Optional<Player> player = playerRepository.findById(id);
        return EntityUtils.unwrapEntity(player, id, "player");
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
    public void updatePlayer(Long id, Player playerDetails) {
        Player player = EntityUtils.unwrapEntity(playerRepository.findById(id), id, "player");
        player.setSeeding(playerDetails.getSeeding());
        player.setUsername(playerDetails.getUsername());
        playerRepository.save(player);
    }

    @Override
    public void deletePlayer(Long id) {
        Optional<Player> player = playerRepository.findById(id);
        if (player.isPresent()) {
            playerRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException("player", id);
        }
    }
    
}
