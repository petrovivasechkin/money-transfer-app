package com.example.transfer.service;

import com.example.transfer.dao.model.AccountObj;
import com.example.transfer.dto.AccountTransfer;
import com.example.transfer.exception.AccountException;
import com.example.transfer.exception.RepositoryException;

import java.util.List;
import java.util.Optional;

/**
 * Created by sergey on 12.10.2018.
 */
public interface AccountService {
    List<AccountObj> getAllAccounts() throws RepositoryException;
    Optional<AccountObj> getAccount(long accountId) throws RepositoryException;
    AccountObj createAccount(String name, int money) throws AccountException, RepositoryException;
    boolean deleteAccount(long accountId) throws RepositoryException;
    boolean transferMoney(AccountTransfer transferRequest) throws AccountException, RepositoryException;

}
