package com.example.transfer.config;

import com.example.transfer.dao.AccountDao;
import com.example.transfer.dao.AccountDaoImpl;
import com.example.transfer.dao.ConnectionFactory;
import com.example.transfer.dao.H2ConnectionFactory;
import com.example.transfer.service.AccountService;
import com.example.transfer.service.AccountServiceImpl;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import javax.inject.Singleton;

/**
 * Created by sergey on 13.10.2018.
 */

public class AccountServiceBinder extends AbstractBinder {
    @Override
    protected void configure() {
        bind(AccountServiceImpl.class).to(AccountService.class).in(Singleton.class );
        bind(H2ConnectionFactory.class).to(ConnectionFactory.class).in(Singleton.class );
        bind(AccountDaoImpl.class).to(AccountDao.class).in(Singleton.class );


    }
}