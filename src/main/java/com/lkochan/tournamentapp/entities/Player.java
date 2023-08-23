package com.lkochan.tournamentapp.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "player", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"tournament_id"})
})
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Player {

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
    private int playedMatches;

    @Min(value = 0, message = "Wins must be positive number.")
    @Column(name = "wins")
    private int wins;

    @Min(value = 0, message = "Draws must be positive number.")
    @Column(name = "draws")
    private int draws;

    @Min(value = 0, message = "Losses must be positive number.")
    @Column(name = "losses")
    private int losses;

    @Min(value = 0, message = "Scored points must be positive number.")
    @Column(name = "scored_points")
    private int scoredPoints;

    @Min(value = 0, message = "Lost points must be positive number.")
    @Column(name = "lost_points")
    private int lostPoints;

    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(name = "tournament_id", referencedColumnName = "id")
    private Tournament tournament;

}
