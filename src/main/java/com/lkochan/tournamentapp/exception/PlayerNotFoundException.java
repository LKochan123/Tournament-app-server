package com.lkochan.tournamentapp.exception;

public class PlayerNotFoundException extends RuntimeException {
    public PlayerNotFoundException(Long id) {
        super("The player id '" + id + "' does not exist in our records");
    }
}
