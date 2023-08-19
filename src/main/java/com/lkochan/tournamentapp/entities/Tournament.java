package com.lkochan.tournamentapp.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tournament")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Tournament {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank(message = "Type can't be blank!")
    @Size(max = 30, message = "Type can have at most 30 characters")
    @Column(name = "type", nullable = false)
    private String type;

    @NotBlank(message = "Status can't be blank!")
    @Size(max = 30, message = "Status can have at most 30 characters")
    @Column(name = "status", nullable = false)
    private String status;

    @Min(value = 2, message = "Bracket size minimum value is 2")
    @Max(value = 16, message = "Bracket size maximum value is 16")
    @Column(name = "bracket_size")
    private int bracketSize;

}
