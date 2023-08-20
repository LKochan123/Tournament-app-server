package com.lkochan.tournamentapp.service.implementation;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.lkochan.tournamentapp.entities.Player;
import com.lkochan.tournamentapp.entities.Tournament;
import com.lkochan.tournamentapp.exception.TournamentNotFoundException;
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
    public List<Player> getPlayers(Long tournament_id) {
        return playerRepository.findByTournamentId(tournament_id);
    }

    @Override
    public Player getPlayer(Long id) {
        Optional<Player> player = playerRepository.findById(id);
        return unwrapPlayer(player, id);
    }

    @Override
    public void savePlayer(Player player, Long tournament_id) {
        Tournament tournament = tournamentRepository.findById(tournament_id).get();
        player.setTournament(tournament);
        playerRepository.save(player);
    }

    @Override
    public void updatePlayer(Long id, Player player) {
        Player p = unwrapPlayer(playerRepository.findById(id), id);
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
            throw new TournamentNotFoundException(id);
        }
    }

    static Player unwrapPlayer(Optional<Player> entity, Long id) {
        if (entity.isPresent()) return entity.get();
        else throw new TournamentNotFoundException(id);
    }
    
}
