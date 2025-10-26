package org.shorty.api.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ServiceException implements ExceptionMapper<RuntimeException>{

    @Override
    public Response toResponse(RuntimeException exception) {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Internal server error: " + exception.getMessage())
                .build();
    }
}
