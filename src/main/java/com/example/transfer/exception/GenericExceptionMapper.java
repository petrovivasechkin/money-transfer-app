package com.example.transfer.exception;

import com.example.transfer.dto.ErrorResponse;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by sergey on 15.10.2018.
 */
@Provider
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger log = Logger.getLogger(GenericExceptionMapper.class.getName());


    @Override
    public Response toResponse(Throwable e) {

        ErrorResponse errorResponse = new ErrorResponse(Collections.singletonList(e.getMessage()));
        Response.Status status;
        if (e instanceof AccountException) {

            log.log(Level.SEVERE, "Incorrect data was provided: ", e);
            status = Response.Status.BAD_REQUEST;

        } else if (e instanceof RepositoryException) {

            log.log(Level.SEVERE, "Repository communication exception: ", e);
            status = Response.Status.INTERNAL_SERVER_ERROR;

        } else {

            log.log(Level.SEVERE, "Unknown problem: ", e);
            status = Response.Status.INTERNAL_SERVER_ERROR;

        }

        return Response
                .status(status)
                .type(MediaType.APPLICATION_JSON)
                .entity(errorResponse)
                .build();
    }

}

