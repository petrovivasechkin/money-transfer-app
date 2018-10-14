package com.example.transfer.exception;

import com.example.transfer.dto.ErrorResponse;

import javax.inject.Singleton;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by sergey on 14.10.2018.
 */

@Provider
public class ConstraintViolationMapper implements ExceptionMapper<ConstraintViolationException> {

    private static final Logger log = Logger.getLogger(ConstraintViolationMapper.class.getName());

    @Override
    public Response toResponse(ConstraintViolationException e) {
        log.log(Level.SEVERE, "Validation failed ", e);
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        List<String> messages = new ArrayList<>();
        for (ConstraintViolation<?> violation : violations) {
            messages.add(violation.getMessage());
        }
        ErrorResponse errorResponse = new ErrorResponse(messages);
        return Response.status(Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON).entity(errorResponse).build();
    }

}