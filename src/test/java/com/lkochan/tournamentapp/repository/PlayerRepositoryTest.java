package com.lkochan.tournamentapp.repository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import static org.assertj.core.api.Assertions.assertThat;

import com.lkochan.tournamentapp.entities.Player;
import com.lkochan.tournamentapp.entities.Tournament;

@DataJpaTest
public class PlayerRepositoryTest {

    @Autowired private PlayerRepository playerRepository;
    @Autowired private TournamentRepository tournamentRepository;

    @Test
    public void findByTournamentIdAndOrderByPointsTest() {
        Tournament tournament = new Tournament("SE", "Open", 4);
        Player player1 = new Player("Masuj", 1, 2, 1, 1, 0, 8, 3, tournament);
        Player player2 = new Player("Misiek", 2, 2, 0, 1, 1, 5, 2, tournament);
        Player player3 = new Player("Bearer", 3, 2, 0, 1, 1, 7, 3, tournament);
        Player player4 = new Player("Bearer", 4, 2, 0, 1, 1, 5, 4, tournament);
        tournamentRepository.save(tournament);
        playerRepository.saveAll(Arrays.asList(player1, player2, player3, player4));

        Optional<List<Player>> result = playerRepository.findByTournamentIdAndOrderByPoints(tournament.getId());

        assertThat(result).isPresent();
        assertThat(result.get()).containsExactly(player1, player3, player2, player4);
    }
}
