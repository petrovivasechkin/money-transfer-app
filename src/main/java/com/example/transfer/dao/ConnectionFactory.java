package com.example.transfer.dao;

import com.example.transfer.exception.InitException;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by sergey on 14.10.2018.
 */
public interface ConnectionFactory {

    Connection getDBConnection() throws SQLException;

    void initDataBase() throws InitException;

}
