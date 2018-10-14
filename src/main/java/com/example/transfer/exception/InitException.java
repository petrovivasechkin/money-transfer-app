package com.example.transfer.exception;

/**
 * Created by sergey on 13.10.2018.
 */
public class InitException extends Exception {

    public InitException(String msg) {
        super(msg);
    }

    public InitException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
