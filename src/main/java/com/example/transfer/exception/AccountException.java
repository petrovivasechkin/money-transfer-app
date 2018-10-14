package com.example.transfer.exception;

/**
 * Created by sergey on 13.10.2018.
 */
public class AccountException extends Exception {

    public AccountException(String msg) {
        super(msg);
    }

    public AccountException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
