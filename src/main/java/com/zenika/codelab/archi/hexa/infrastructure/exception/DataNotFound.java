package com.zenika.codelab.archi.hexa.infrastructure.exception;

public class DataNotFound extends RuntimeException {
    public DataNotFound(String code) {
        super(String.format("Pas de donnée en base pour le model %s", code));
    }
}
