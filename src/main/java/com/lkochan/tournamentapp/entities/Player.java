package com.lkochan.tournamentapp.entities;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
// @Table(name = "player", uniqueConstraints = {
//     @UniqueConstraint(columnNames = {"tournament_id"})
// })
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Player {

    public Player(
        Long id, String username, int seeding, int matches, int wins, int draws, int losses, int scoredPoints, 
        int lostPoints, Tournament tournament
        ) {
        this(username, seeding, matches, wins, draws, losses, scoredPoints, lostPoints, tournament);
        this.id = id;
    }

    public Player(
        String username, int seeding, int matches, int wins, int draws, int losses, int scoredPoints, int lostPoints, Tournament tournament
        ) {
        this.username = username;
        this.seeding = seeding;
        this.playedMatches = matches;
        this.wins = wins;
        this.draws = draws;
        this.losses = losses;
        this.scoredPoints = scoredPoints;
        this.lostPoints = lostPoints;
        this.tournament = tournament;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank(message = "Username can't be blank.")
    @Size(max = 30, message = "Username can have at most 30 characters.")
    @Column(name = "username")
    private String username;

    @Min(value = 1, message = "Seeding number should be integer 1-16")
    @Max(value = 16, message = "Seeding number should be integer 1-16")
    @Column(name = "seeding")
    private int seeding;

    @Min(value = 0, message = "Played matches must be positive number.")
    @Column(name = "played_matches")
    private int playedMatches = 0;

    @Min(value = 0, message = "Wins must be positive number.")
    @Column(name = "wins")
    private int wins = 0;

    @Min(value = 0, message = "Draws must be positive number.")
    @Column(name = "draws")
    private int draws = 0;

    @Min(value = 0, message = "Losses must be positive number.")
    @Column(name = "losses")
    private int losses = 0;

    @Min(value = 0, message = "Scored points must be positive number.")
    @Column(name = "scored_points")
    private int scoredPoints = 0;

    @Min(value = 0, message = "Lost points must be positive number.")
    @Column(name = "lost_points")
    private int lostPoints = 0;

    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(name = "tournament_id", referencedColumnName = "id")
    private Tournament tournament;

    @JsonIgnore
    @OneToMany(mappedBy = "playerOne", cascade = CascadeType.ALL)
    private List<Match> matchesAsPlayerOne;

    @JsonIgnore
    @OneToMany(mappedBy = "playerTwo", cascade = CascadeType.ALL)
    private List<Match> matchesAsPlayerTwo;

}
