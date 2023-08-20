package com.lkochan.tournamentapp.service.interfaces;

import java.util.List;

import com.lkochan.tournamentapp.entities.Player;

public interface PlayerService {
    List<Player> getPlayers(Long tournament_id);
    Player getPlayer(Long id);
    void savePlayer(Player player, Long tournament_id);
    void updatePlayer(Long id, Player player);
    void deletePlayer(Long id);
}
