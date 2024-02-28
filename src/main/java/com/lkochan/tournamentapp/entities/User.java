package com.lkochan.tournamentapp.entities;

import com.lkochan.tournamentapp.enums.UserRole;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor
public class User {

    public User(Long id, String username, String password, String email) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NonNull
    @NotBlank(message = "Username can't be blank!")
    @Size(min = 4, message = "Username must contains at least 4 letters")
    @Size(max = 20, message = "Username can have at most 30 characters")
    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @NonNull
    @NotBlank(message = "Password can't be blank!")
    @Size(min = 4, message = "Password must contains at least 4 characters")
    @Column(nullable = false)
    private String password;

    @NonNull
    @NotBlank(message = "Email can't be blank!")
    @Column(name = "email", unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private UserRole role = UserRole.USER;

    @Column(name = "is_banned")
    private Boolean isBanned = false;

}
