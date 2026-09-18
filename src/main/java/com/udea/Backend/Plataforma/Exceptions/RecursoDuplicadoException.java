package com.udea.Backend.Plataforma.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción lanzada cuando se intenta crear o actualizar un recurso
 * violando una restricción de unicidad (duplicados). Mapea a HTTP 409 Conflict.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class RecursoDuplicadoException extends RuntimeException {

    public RecursoDuplicadoException(String message) {
        super(message);
    }
}
