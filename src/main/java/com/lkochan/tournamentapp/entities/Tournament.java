package com.lkochan.tournamentapp.entities;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "tournament")
@RequiredArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Tournament {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NonNull
    @NotBlank(message = "Type can't be blank!")
    @Size(max = 30, message = "Type can have at most 30 characters")
    @Column(name = "type", nullable = false)
    private String type;

    @NonNull
    @NotBlank(message = "Status can't be blank!")
    @Size(max = 30, message = "Status can have at most 30 characters")
    @Column(name = "status", nullable = false)
    private String status;

    @Min(value = 4, message = "Bracket size minimum value is 2")
    @Max(value = 16, message = "Bracket size maximum value is 16")
    @Column(name = "bracket_size")
    private int bracketSize;

    @JsonIgnore
    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL)
    private List<Player> players;

}
