package com.example.transfer.dao;

import com.example.transfer.exception.InitException;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by sergey on 12.10.2018.
 */
@Singleton
public class H2ConnectionFactory implements ConnectionFactory{
    private static final Logger log = Logger.getLogger(H2ConnectionFactory.class.getName());

    private static final String DB_CONNECTION = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";
    private static final String DB_USER = "";
    private static final String DB_PASSWORD = "";


    public Connection getDBConnection() throws SQLException {
        return DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD);
    }

    @PostConstruct
    public void initDataBase() throws InitException {
        String createQuery;
        FileSystem fs = null;
        try {
            ClassLoader classLoader = H2ConnectionFactory.class.getClassLoader();
            URI initDbScriptUri = classLoader.getResource("initDb.sql").toURI();

            Path path;
            if ("jar".equals(initDbScriptUri.getScheme())) {
                log.log(Level.INFO, "Try to find initial DB script inside jar");
                Map<String, String> env = new HashMap<>();
                String[] array = initDbScriptUri.toString().split("!");
                fs = FileSystems.newFileSystem(URI.create(array[0]), env);
                path = fs.getPath(array[1]);
            } else {
                log.log(Level.INFO, "Try to find initial DB script near me");
                path = Paths.get(initDbScriptUri);
            }
            createQuery = new String(Files.readAllBytes(path));
        } catch (URISyntaxException | IOException e) {
            throw new InitException("Cant read initial DB script", e);
        } finally {
            try {
                if (fs != null) {
                    fs.close();
                }
            } catch (IOException e) {
                log.log(Level.SEVERE, "Cant close filesystem after readin db script", e);
            }
        }

        try (Connection conn = getDBConnection();
             PreparedStatement ps = conn.prepareStatement(createQuery)) {
             ps.executeUpdate();
             log.log(Level.INFO, "DB script run successfully");
        } catch (SQLException e) {
            throw new InitException("Exception while running Database script", e);
        }

    }
}
