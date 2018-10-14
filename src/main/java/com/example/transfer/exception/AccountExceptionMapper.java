package com.example.transfer.exception;

import com.example.transfer.dao.AccountDaoImpl;
import com.example.transfer.dto.ErrorResponse;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by sergey on 13.10.2018.
 */
@Provider
public class AccountExceptionMapper implements ExceptionMapper<AccountException> {

    private static final Logger log = Logger.getLogger(AccountExceptionMapper.class.getName());

    @Override
    public Response toResponse(AccountException e) {
        log.log(Level.SEVERE, "Incorrect data was provided: ", e);

        ErrorResponse errorResponse = new ErrorResponse(Arrays.asList(e.getMessage()));
        return Response
                .status(Response.Status.BAD_REQUEST.getStatusCode())
                .type(MediaType.APPLICATION_JSON)
                .entity(errorResponse)
                .build();
    }
}
