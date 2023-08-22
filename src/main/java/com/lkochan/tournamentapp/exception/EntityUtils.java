package com.lkochan.tournamentapp.exception;

import java.util.Optional;

public class EntityUtils {
    public static <T> T unwrapEntity(Optional<T> entity, Long id, String name) {
        return entity.orElseThrow(() -> new EntityNotFoundException(name, id));
    }
}
