package com.lkochan.tournamentapp.exception;

public class MatchNotFoundException extends RuntimeException {
    public MatchNotFoundException(Long tournamentId, Long playerOneId, Long playerTwoId) {
        super(String.format(
            "The match not found. Tournament id (%s) or one of players id's (%s, %s) is incorrect.",
            tournamentId, playerOneId, playerTwoId)
        );
    }
}
