package com.example.transfer.controller;

import com.example.transfer.dao.model.AccountObj;
import com.example.transfer.dto.Account;
import com.example.transfer.dto.ErrorResponse;
import com.example.transfer.exception.AccountException;
import com.example.transfer.exception.RepositoryException;
import com.example.transfer.service.AccountService;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Created by sergey on 13.10.2018.
 */

@Path("/accountService")
public class AccountController {

    private static final Logger log = Logger.getLogger(AccountController.class.getName());

    @Inject
    private AccountService accountService;


    public AccountController() {
    }

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GET
    @Path("allaccounts")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getAllAccounts() throws RepositoryException {
        log.log(Level.INFO, "Got request for all accounts");
        List<AccountObj> accountObjs = accountService.getAllAccounts();
        if (accountObjs.isEmpty()) {
            ErrorResponse errorResponse = new ErrorResponse("Accounts empty");
            return Response.status(Response.Status.NOT_FOUND)
                    .type(MediaType.APPLICATION_JSON).entity(errorResponse).build();
        } else {
            List<Account> accounts = new ArrayList<>();
            accountObjs.forEach((a) -> accounts.add(new Account(a)));
            return Response.status(Response.Status.OK).type(MediaType.APPLICATION_JSON).entity(accounts).build();
        }

    }

    @GET
    @Path("account/{accountId}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getAccount(@PathParam("accountId") @Min(1) long accountId) throws RepositoryException {
        log.log(Level.INFO, "Got request for account, accountId:{}", accountId);
        Optional<AccountObj> accountOptional = accountService.getAccount(accountId);
        if (accountOptional.isPresent()) {
            Account account = new Account(accountOptional.get());
            return Response.status(Response.Status.OK).type(MediaType.APPLICATION_JSON).entity(account).build();
        } else {
            ErrorResponse errorResponse = new ErrorResponse("Account is not exist");
            return Response.status(Response.Status.NOT_FOUND).type(MediaType.APPLICATION_JSON).entity(errorResponse).build();
        }

    }

    @POST
    @Path("account")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Account createAccount(@Valid @NotNull Account account) throws AccountException, RepositoryException {
        log.log(Level.INFO, "Got request for creating new account, account:{}", account);
        AccountObj accountObj = accountService.createAccount(account.getName(), account.getMoney());
        return new Account(accountObj);

    }

    @DELETE
    @Path("account/{accountId}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response deleteAccount(@PathParam("accountId") @Min(1) long accountId) throws RepositoryException {
        log.log(Level.INFO, "Got request for deleting account, accountId:{}", accountId);
        boolean result = accountService.deleteAccount(accountId);
        if (result) {
            return Response.status(Response.Status.OK).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

    }

}
