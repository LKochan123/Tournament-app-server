package com.lkochan.tournamentapp.exception;

public class TournamentNotFoundException extends RuntimeException {
    public TournamentNotFoundException(Long id) {
        super("The tournament id '" + id + "' does not exist in our records");
    }
}
