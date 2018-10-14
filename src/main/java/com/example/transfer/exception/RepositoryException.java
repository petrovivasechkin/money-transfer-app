package com.example.transfer.exception;

/**
 * Created by sergey on 14.10.2018.
 */
public class RepositoryException extends Exception{

    public RepositoryException(String msg) {
        super(msg);
    }

    public RepositoryException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
