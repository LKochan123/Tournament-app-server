package com.lkochan.tournamentapp.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @NotBlank(message = "Username can't be blank!")
    @Column(name = "username")
    private String username;

    @Column(name = "seeding")
    private int seeding;

    @Column(name = "played_matches")
    private int playedMatches;

    @Column(name = "wins")
    private int wins;

    @Column(name = "losses")
    private int losses;

    @Column(name = "scored_points")
    private int scoredPoints;

    @Column(name = "lost_points")
    private int lostPoints;

    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(name = "tournament_id", referencedColumnName = "id")
    private Tournament tournament;

}
