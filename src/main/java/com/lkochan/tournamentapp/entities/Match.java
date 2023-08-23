package com.lkochan.tournamentapp.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;

@Entity
@Table(name = "match", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"tournament_id", "round_number", "player_one_id", "player_two_id"})
})
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "tournament_id", referencedColumnName = "id")
    private Tournament tournament;

    @Column(name = "round_number")
    private int roundNumber;

    @ManyToOne
    @JoinColumn(name = "player_one_id", referencedColumnName = "id")
    private Player playerOne;

    @ManyToOne
    @JoinColumn(name = "player_two_id", referencedColumnName = "id")
    private Player playerTwo;

    @Min(value = 0, message = "Player score can't be negative")
    @Column(name = "p_one_score")
    private int playerOneScore;

    @Min(value = 0, message = "Player score can't be negative")
    @Column(name = "p_two_score")
    private int playerTwoScore;

}
