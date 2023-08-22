package com.lkochan.tournamentapp.exception;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String entityName, Long id) {
        super("The " + entityName + " with id '" + id + "' does not exist in our records");
    }
}
