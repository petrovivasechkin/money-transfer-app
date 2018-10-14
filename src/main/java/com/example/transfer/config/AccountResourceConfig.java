package com.example.transfer.config;

import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ext.Provider;

/**
 * Created by sergey on 13.10.2018.
 */
@Provider
public class AccountResourceConfig extends ResourceConfig {
    public AccountResourceConfig() {
        packages(true,"com.example.transfer");
        register(new AccountServiceBinder());
    }


}