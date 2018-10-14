package com.example.transfer.exception;

import com.example.transfer.dto.ErrorResponse;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by sergey on 14.10.2018.
 */
@Provider
public class RepositoryExceptionMapper implements ExceptionMapper<RepositoryException> {

    private static final Logger log = Logger.getLogger(RepositoryExceptionMapper.class.getName());


    @Override
    public Response toResponse(RepositoryException e) {
        log.log(Level.SEVERE, "Repository communication exception: ", e);
        ErrorResponse errorResponse = new ErrorResponse(Arrays.asList(e.getMessage()));
        return Response
                .status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode())
                .type(MediaType.APPLICATION_JSON)
                .entity(errorResponse)
                .build();
    }
}
