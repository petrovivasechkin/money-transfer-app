package com.example.transfer.controller;

import com.example.transfer.dto.AccountTransfer;
import com.example.transfer.exception.AccountException;
import com.example.transfer.exception.RepositoryException;
import com.example.transfer.service.AccountService;
import org.junit.Test;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by sergey on 14.10.2018.
 */
public class TransferControllerTest {

    @Test
    public void transferMoneyTest() throws AccountException, RepositoryException {

        AccountService accountServiceMock = mock(AccountService.class);
        TransferController transferController = new TransferController(accountServiceMock);

        AccountTransfer transferReq = new AccountTransfer(1, 2, 10);
        when(accountServiceMock.transferMoney(transferReq))
                .thenReturn(true);

        Response actualRes = transferController.transferMoney(transferReq);
        assertEquals(Response.Status.OK, actualRes.getStatusInfo());
    }


    @Test
    public void unseccessTransferMoneyTest() throws AccountException, RepositoryException {

        AccountService accountServiceMock = mock(AccountService.class);
        TransferController transferController = new TransferController(accountServiceMock);

        AccountTransfer transferReq = new AccountTransfer(1, 2, 10);
        when(accountServiceMock.transferMoney(transferReq))
                .thenReturn(false);

        Response actualRes = transferController.transferMoney(transferReq);
        assertEquals(Response.Status.BAD_REQUEST, actualRes.getStatusInfo());
    }

}
