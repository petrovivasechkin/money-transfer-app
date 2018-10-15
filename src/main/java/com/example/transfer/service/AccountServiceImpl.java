package com.example.transfer.service;

import com.example.transfer.dao.AccountDao;
import com.example.transfer.dao.AccountDaoImpl;
import com.example.transfer.dao.H2ConnectionFactory;
import com.example.transfer.dao.model.AccountObj;
import com.example.transfer.dto.AccountTransfer;
import com.example.transfer.exception.AccountException;
import com.example.transfer.exception.RepositoryException;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.Path;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by sergey on 12.10.2018.
 */

@Singleton
public class AccountServiceImpl implements AccountService {

    private static final Logger log = Logger.getLogger(AccountServiceImpl.class.getName());

    @Inject
    private AccountDao accountDao;


    public AccountServiceImpl() {
        this(new AccountDaoImpl());
    }

    public AccountServiceImpl(AccountDao accountDao) {
        this.accountDao = accountDao;
    }


    public List<AccountObj> getAllAccounts() throws RepositoryException {
        return accountDao.getAllAccountObj();
    }

    @Override
    public Optional<AccountObj> getAccount(long accountId) throws RepositoryException {
        return accountDao.getAccountObj(accountId);
    }

    @Override
    public AccountObj createAccount(String name, int money) throws AccountException, RepositoryException {
        return accountDao.createNewAccountObj(name, money);
    }

    @Override
    public boolean deleteAccount(long accountId) throws RepositoryException {
        int rowsUpdated = accountDao.deleteAccountObj(accountId);
        if (rowsUpdated == 1) {
            log.log(Level.INFO, "Deleted id:{0}",accountId);
            return true;
        } else {
            log.log(Level.INFO, "Not Deleted id:{0}",accountId);
            return false;
        }

    }

    @Override
    public boolean transferMoney(AccountTransfer accountTransfer) throws AccountException, RepositoryException {
        int rowsUpdated = accountDao.transferMoney(accountTransfer.getFromAccountId(),
                accountTransfer.getToAccountId(), accountTransfer.getMoney());
        if (rowsUpdated == 2) {
            log.log(Level.INFO, "Transfer completed, accountTransfer:{0}",accountTransfer);
            return true;
        } else {
            log.log(Level.INFO, "Transfer wasnt done, accountTransfer:{0}",accountTransfer);
            return false;
        }
    }


    public void setAccountDao(AccountDao accountDao) {
        this.accountDao = accountDao;
    }
}
