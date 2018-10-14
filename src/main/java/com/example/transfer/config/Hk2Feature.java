package com.example.transfer.config;

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;

/**
 * Created by sergey on 13.10.2018.
 */

@Provider
public class Hk2Feature implements Feature {
    @Override
    public boolean configure(FeatureContext context) {
        context.register(new AccountServiceBinder());
        return true;
    }
}