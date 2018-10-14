package com.example.transfer;

import com.example.transfer.dao.ConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;

import java.util.logging.*;

/**
 * Created by sergey on 12.10.2018.
 */
public class Main {

    private static final Logger log = Logger.getLogger(ConnectionFactory.class.getName());

    public static void initLogger() {
        LogManager logManager = LogManager.getLogManager();
        try {
            ClassLoader classLoader = Main.class.getClassLoader();
            logManager.readConfiguration(classLoader.getResourceAsStream("logger.properties"));
        } catch (Exception e) {
            logManager.reset();
            Handler handler = new ConsoleHandler();
            handler.setFormatter(new SimpleFormatter());
            log.addHandler(handler);
        }
    }


    public static void main(String[] args) throws Exception {
        initLogger();

        Server server = new Server(8080);

        ServletContextHandler ctx =
                new ServletContextHandler(ServletContextHandler.NO_SESSIONS);

        ctx.setContextPath("/");
        server.setHandler(ctx);

        ServletHolder serHol = ctx.addServlet(ServletContainer.class, "/*");
        serHol.setInitOrder(1);
        serHol.setInitParameter("jersey.config.server.provider.packages",
                "com.example.transfer");

        try {
            server.start();
            server.join();
        } finally {
            server.destroy();
        }
    }


}
