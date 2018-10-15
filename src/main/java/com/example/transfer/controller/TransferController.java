package com.example.transfer.controller;

import com.example.transfer.dto.AccountTransfer;
import com.example.transfer.exception.AccountException;
import com.example.transfer.exception.RepositoryException;
import com.example.transfer.service.AccountService;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by sergey on 13.10.2018.
 */

@Path("/transferService")
public class TransferController {
    private static final Logger log = Logger.getLogger(TransferController.class.getName());

    @Inject
    private AccountService accountService;

    public TransferController() {
    }

    public TransferController(AccountService accountService) {
        this.accountService = accountService;
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    public Response transferMoney(@Valid @NotNull AccountTransfer accountTransfer) throws AccountException, RepositoryException {
        log.log(Level.INFO, "Got transfer request, accountTransfer:{0}", accountTransfer);
        if (accountService.transferMoney(accountTransfer)) {
            return Response.status(Response.Status.OK).build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }


}
