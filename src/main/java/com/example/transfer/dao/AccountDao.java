package com.example.transfer.dao;

import com.example.transfer.dao.model.AccountObj;
import com.example.transfer.exception.AccountException;
import com.example.transfer.exception.RepositoryException;

import java.util.List;
import java.util.Optional;

/**
 * Created by sergey on 12.10.2018.
 */
public interface AccountDao {

    Optional<AccountObj> getAccountObj(long accountId) throws RepositoryException;

    List<AccountObj> getAllAccountObj() throws RepositoryException;

    AccountObj createNewAccountObj(String name, int money) throws AccountException, RepositoryException;

    int deleteAccountObj(long accountId) throws RepositoryException;

    int transferMoney(long fromId, long toId, int money) throws AccountException, RepositoryException;

    int addMoney(long toId, int addMoney) throws AccountException, RepositoryException;
}
