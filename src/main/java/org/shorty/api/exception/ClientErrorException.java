package org.shorty.api.exception;

import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ClientErrorException implements ExceptionMapper<BadRequestException> {

    @Override
    public jakarta.ws.rs.core.Response toResponse(BadRequestException exception) {
        return jakarta.ws.rs.core.Response.status(jakarta.ws.rs.core.Response.Status.BAD_REQUEST)
                .entity("Client error: " + exception.getMessage())
                .build();
    }

}
