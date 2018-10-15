package com.example.transfer.dao;

import com.example.transfer.dao.model.AccountObj;
import com.example.transfer.exception.AccountException;
import com.example.transfer.exception.InitException;
import com.example.transfer.exception.RepositoryException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.Thread.sleep;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 * Created by sergey on 14.10.2018.
 */
public class AccountDaoTest {

    private static final Logger log = Logger.getLogger(H2ConnectionFactory.class.getName());
    private static final String DB_CONNECTION = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";
    private static final String DB_USER = "";
    private static final String DB_PASSWORD = "";
    private static final String DROP_DB = "DROP ALL OBJECTS";
    private static final String TRUNC_DB = "TRUNCATE TABLE Account";
    private static final String LOCK_DB_ROW = "SELECT money FROM Account where id=? FOR UPDATE";

    private static AccountDao accountDao;

    private static ConnectionFactory connectionFactory;

    @BeforeClass
    public static void init() throws Exception {


        connectionFactory = new ConnectionFactory() {

            @Override
            public Connection getDBConnection() throws SQLException {
                return DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD);
            }

            @Override
            public void initDataBase() throws InitException {
                try {
                    ClassLoader classLoader = getClass().getClassLoader();
                    URI initDbScriptUri = classLoader.getResource("initTestDb.sql").toURI();
                    Path path = Paths.get(initDbScriptUri);
                    String createQuery = new String(Files.readAllBytes(path));

                    try (Connection conn = getDBConnection();
                         PreparedStatement ps = conn.prepareStatement(createQuery)) {
                        ps.executeUpdate();
                        log.log(Level.INFO, "DB script run successfully");
                    }
                } catch (Exception e) {
                    throw new InitException("Test initialization failed", e);
                }
            }
        };
        connectionFactory.initDataBase();

        accountDao = new AccountDaoImpl(connectionFactory);
    }

    @Before
    public void before() throws InitException {
        updateDBCommand(DROP_DB);
        connectionFactory.initDataBase();
    }

    @Test
    public void getAccountObj() throws RepositoryException {
        long testAccId = 1;
        Optional<AccountObj> accountObjOpt = accountDao.getAccountObj(testAccId);
        AccountObj expected = new AccountObj(1, "Vasya", 55);
        assertEquals(expected, accountObjOpt.get());
    }

    @Test
    public void getAccountObjEmpty() throws RepositoryException {
        long testAccId = -1;
        Optional<AccountObj> accountObjOpt = accountDao.getAccountObj(testAccId);
        assertFalse(accountObjOpt.isPresent());
    }

    @Test
    public void getAllAccountObj() throws RepositoryException {
        List<AccountObj> expectedAccountObjList = new ArrayList<>();
        expectedAccountObjList.add(new AccountObj(1, "Vasya", 55));
        expectedAccountObjList.add(new AccountObj(2, "Petya", 60));
        expectedAccountObjList.add(new AccountObj(3, "Vova", 30));
        expectedAccountObjList.add(new AccountObj(4, "Tolya", 100));

        List<AccountObj> accountObjList = accountDao.getAllAccountObj();
        assertEquals(expectedAccountObjList, accountObjList);
    }

    @Test
    public void getAllAccountObjEmpty() throws RepositoryException {
        updateDBCommand(TRUNC_DB);
        List<AccountObj> accountObjList = accountDao.getAllAccountObj();
        assertEquals(Collections.<AccountObj>emptyList(), accountObjList);
    }

    @Test
    public void createNewAccountObj() throws AccountException, RepositoryException {
        AccountObj expected = new AccountObj(5, "Simon", 999);
        AccountObj accountObj = accountDao.createNewAccountObj("Simon", 999);
        Optional<AccountObj> accountObjOpt = accountDao.getAccountObj(accountObj.getId());

        assertEquals(expected, accountObjOpt.get());
        assertEquals(expected, accountObj);
    }

    @Test
    public void createNewAccountObjDuplication() throws RepositoryException {
        boolean wasThrown = false;
        AccountException expectedEx = null;
        try {
            accountDao.createNewAccountObj("Vasya", 55);
        } catch (AccountException e) {
            wasThrown = true;
            expectedEx = e;
        }
        assertTrue(wasThrown);
        assertEquals("Account duplication", expectedEx.getMessage());
    }

    @Test
    public void deleteAccountObj() throws RepositoryException {
        List<AccountObj> accountObjList = accountDao.getAllAccountObj();
        assertEquals(4, accountObjList.size());

        int rowsUpdated = accountDao.deleteAccountObj(1);
        assertEquals(1, rowsUpdated);

        accountObjList = accountDao.getAllAccountObj();
        assertEquals(3, accountObjList.size());
    }

    @Test
    public void deleteAccountObjNotExist() throws RepositoryException {
        List<AccountObj> accountObjList = accountDao.getAllAccountObj();
        assertEquals(4, accountObjList.size());

        int rowsUpdated = accountDao.deleteAccountObj(100);
        assertEquals(0, rowsUpdated);

        accountObjList = accountDao.getAllAccountObj();
        assertEquals(4, accountObjList.size());
    }

    @Test
    public void transferMoney() throws AccountException, RepositoryException {
        Optional<AccountObj> firstObjOptBeforeUpdate = accountDao.getAccountObj(1);
        Optional<AccountObj> secondObjOptBeforeUpdate = accountDao.getAccountObj(2);
        int sum = 30;
        int rowsUpdated = accountDao.transferMoney(1, 2, sum);

        assertEquals(2, rowsUpdated);

        Optional<AccountObj> firstObjOptAfterUpdate = accountDao.getAccountObj(1);
        Optional<AccountObj> secondObjOptAfterUpdate = accountDao.getAccountObj(2);

        assertEquals(firstObjOptBeforeUpdate.get().getMoney() - sum,
                firstObjOptAfterUpdate.get().getMoney());

        assertEquals(secondObjOptBeforeUpdate.get().getMoney() + sum,
                secondObjOptAfterUpdate.get().getMoney());
    }

    @Test
    public void transferMoneyInsufficient() throws AccountException, RepositoryException {
        Optional<AccountObj> firstObjOptBeforeUpdate = accountDao.getAccountObj(1);
        Optional<AccountObj> secondObjOptBeforeUpdate = accountDao.getAccountObj(2);
        int sum = 300;
        boolean wasThrown = false;
        AccountException expectedEx = null;
        try {
            accountDao.transferMoney(1, 2, sum);
        } catch (AccountException e) {
            wasThrown = true;
            expectedEx = e;
        }
        assertTrue(wasThrown);
        assertEquals("Insufficient funds on source account id: " + firstObjOptBeforeUpdate.get().getId(),
                expectedEx.getMessage());

        Optional<AccountObj> firstObjOptAfterUpdate = accountDao.getAccountObj(1);
        Optional<AccountObj> secondObjOptAfterUpdate = accountDao.getAccountObj(2);

        assertEquals(firstObjOptBeforeUpdate.get().getMoney(),
                firstObjOptAfterUpdate.get().getMoney());

        assertEquals(secondObjOptBeforeUpdate.get().getMoney(),
                secondObjOptAfterUpdate.get().getMoney());
    }


    @Test
    public void transferMoneyLockedRowsException() throws AccountException, RepositoryException, SQLException {
        Optional<AccountObj> firstObjOptBeforeUpdate = accountDao.getAccountObj(1);
        Optional<AccountObj> secondObjOptBeforeUpdate = accountDao.getAccountObj(2);

        int sum = 30;
        long lockedId = 2;
        boolean wasThrown = false;


        try (Connection conn = connectionFactory.getDBConnection();
             PreparedStatement ps = conn.prepareStatement(LOCK_DB_ROW)) {
            conn.setAutoCommit(false);
            ps.setLong(1, lockedId);
            ps.executeQuery();
            accountDao.transferMoney(1, 2, sum);
            try {
                sleep(1001);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            conn.commit();
            conn.setAutoCommit(true);
        } catch (RepositoryException e) {
            wasThrown = true;
        }

        assertTrue(wasThrown);
        Optional<AccountObj> firstObjOptAfterUpdate = accountDao.getAccountObj(1);
        Optional<AccountObj> secondObjOptAfterUpdate = accountDao.getAccountObj(2);

        assertEquals(firstObjOptBeforeUpdate.get().getMoney(), firstObjOptAfterUpdate.get().getMoney());
        assertEquals(secondObjOptBeforeUpdate.get().getMoney(), secondObjOptAfterUpdate.get().getMoney());
    }


    @Test
    public void addMoney() throws AccountException, RepositoryException {
        Optional<AccountObj> accountObjOptBeforeUpdate = accountDao.getAccountObj(1);
        assertTrue(accountObjOptBeforeUpdate.isPresent());
        int rowsUpdated = accountDao.addMoney(1, 50);
        assertEquals(1, rowsUpdated);

        Optional<AccountObj> accountObjOptAfterUpdate = accountDao.getAccountObj(1);
        assertEquals(accountObjOptBeforeUpdate.get().getMoney() + 50,
                accountObjOptAfterUpdate.get().getMoney());
    }

    private void updateDBCommand(String query) {
        try (Connection conn = connectionFactory.getDBConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
