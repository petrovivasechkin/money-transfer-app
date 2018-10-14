package com.example.transfer.controller;

import com.example.transfer.dao.model.AccountObj;
import com.example.transfer.dto.Account;
import com.example.transfer.dto.ErrorResponse;
import com.example.transfer.exception.AccountException;
import com.example.transfer.exception.RepositoryException;
import com.example.transfer.service.AccountService;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by sergey on 13.10.2018.
 */
public class AccountControllerTest {

    private AccountController accountController;
    private AccountService accountServiceMock;

    @Before
    public void before() throws Exception {

        accountServiceMock = mock(AccountService.class);
        accountController = new AccountController(accountServiceMock);
    }

    @Test
    public void getAllAccountsTest() throws RepositoryException {

        List<AccountObj> mockAccountObjList = new ArrayList<>();
        mockAccountObjList.add(new AccountObj(1, "Ivan Petrovich", 100));
        mockAccountObjList.add(new AccountObj(2, "Fedor Mihalych", 200));
        mockAccountObjList.add(new AccountObj(3, "Innokentiy Vladimirovich", 300));


        when(accountServiceMock.getAllAccounts()).thenReturn(mockAccountObjList);


        List<Account> expecteAaccounts = new ArrayList<>();
        mockAccountObjList.forEach((a) -> expecteAaccounts.add(new Account(a)));

        Response response = accountController.getAllAccounts();
        assertEquals(Response.Status.OK, response.getStatusInfo());
        assertEquals(expecteAaccounts, response.getEntity());

    }

    @Test
    public void getEmptyAllAccountsTest() throws RepositoryException {

        List<AccountObj> mockAccountObjList = new ArrayList<>();

        when(accountServiceMock.getAllAccounts()).thenReturn(mockAccountObjList);

        ErrorResponse expectedResponse = new ErrorResponse("Accounts empty");

        Response response = accountController.getAllAccounts();
        assertEquals(Response.Status.NOT_FOUND, response.getStatusInfo());
        assertEquals(expectedResponse, response.getEntity());

    }


    @Test
    public void getEmptyAccountsTest() throws RepositoryException {

        AccountObj accountObj = new AccountObj(1, "Petya", 10);
        when(accountServiceMock.getAccount(anyLong()))
                .thenReturn(Optional.ofNullable(null));
        ErrorResponse errorResponse = new ErrorResponse("Account is not exist");

        Response response = accountController.getAccount(1);
        assertEquals(Response.Status.NOT_FOUND, response.getStatusInfo());
        assertEquals(errorResponse, response.getEntity());

    }


    @Test
    public void getAccountsTest() throws RepositoryException {

        AccountObj accountObj = new AccountObj(1, "Petya", 10);
        when(accountServiceMock.getAccount(anyLong()))
                .thenReturn(Optional.of(accountObj));


        Response response = accountController.getAccount(1);
        assertEquals(Response.Status.OK, response.getStatusInfo());
        assertEquals(new Account(accountObj), response.getEntity());

    }

    @Test
    public void getCreateAccountTest() throws RepositoryException, AccountException {
        AccountObj accountObj = new AccountObj(1, "Petya", 10);
        when(accountServiceMock.createAccount("Petya", 10))
                .thenReturn(accountObj);
        Account accTest = new Account("Petya", 10);

        Account actualAcc = accountController.createAccount(accTest);
        assertEquals(new Account(accountObj), actualAcc);

    }


    @Test
    public void getDeleteAccountTest() throws RepositoryException {
        when(accountServiceMock.deleteAccount(1))
                .thenReturn(true);

        Response actualResp = accountController.deleteAccount(1);
        assertEquals(Response.Status.OK, actualResp.getStatusInfo());
    }

    @Test
    public void getNotExistDeleteAccountTest() throws RepositoryException {
        when(accountServiceMock.deleteAccount(1))
                .thenReturn(false);

        Response actualResp = accountController.deleteAccount(1);
        assertEquals(Response.Status.NOT_FOUND, actualResp.getStatusInfo());
    }
}
