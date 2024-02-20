package com.lkochan.tournamentapp.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.lkochan.tournamentapp.entities.Match;
import com.lkochan.tournamentapp.entities.Player;
import com.lkochan.tournamentapp.entities.Tournament;

@DataJpaTest
public class MatchRepositoryTest {
    
    @Autowired MatchRepository matchRepository;
    @Autowired TournamentRepository tournamentRepository;
    @Autowired PlayerRepository playerRepository;

    private Tournament tournament;
    private List<Player> players;
    private List<Match> matches;

    @BeforeEach
    public void setUp() {
        tournament = createTournament("DE", "Open", 4);
        players = createPlayers(tournament);
        matches = createMatches(players);
    }

    @AfterEach
    public void tearDown() {
        matchRepository.deleteAll();
        playerRepository.deleteAll();
        tournamentRepository.deleteAll();
    }

    @Test
    public void findByPlayerIdTest() {
        Optional<List<Match>> result = matchRepository.findByPlayerId(players.get(3).getId());

        assertThat(result).isPresent();
        assertThat(result.get()).containsExactly(matches.get(1), matches.get(2));
    }

    @Test
    public void findHeadToHeadMatchesTest() {
        Long playerOneId = players.get(0).getId();
        Long playerTwoId = players.get(1).getId();

        Optional<List<Match>> result = matchRepository.findHeadToHeadMatches(playerOneId, playerTwoId);

        assertThat(result).isPresent();
        assertThat(result.get()).containsExactly(matches.get(0), matches.get(3));
    }

    private Tournament createTournament(String type, String status, int bracketSize) {
        Tournament tournament = new Tournament(type, status, bracketSize);
        tournamentRepository.save(tournament);
        return tournament;
    }

    private List<Player> createPlayers(Tournament tournament) {
        Player player1 = new Player("Masuj", 1, 0, 0, 0, 0, 0, 0, tournament);
        Player player2 = new Player("Misiek", 2, 0, 0, 0, 0, 0, 0, tournament);
        Player player3 = new Player("Bearer", 3, 0, 0, 0, 0, 0, 0, tournament);
        Player player4 = new Player("Cowy", 4, 2, 0, 0, 0, 0, 0, tournament);
        List<Player> players = Arrays.asList(player1, player2, player3, player4);
        playerRepository.saveAll(players);
        return players;
    }

    private List<Match> createMatches(List<Player> players) {
        Match match1 = new Match(players.get(0), 3, players.get(1), 2);
        Match match2 = new Match(players.get(1), 2, players.get(3), 4);
        Match match3 = new Match(players.get(3), 1, players.get(2), 1);
        Match match4 = new Match(players.get(1), 5, players.get(0), 5);
        List<Match> matches = Arrays.asList(match1, match2, match3, match4);
        matchRepository.saveAll(matches);
        return matches;
    }
}
