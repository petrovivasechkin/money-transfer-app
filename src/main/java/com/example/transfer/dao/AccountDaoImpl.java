package com.example.transfer.dao;

import com.example.transfer.dao.model.AccountObj;
import com.example.transfer.exception.AccountException;
import com.example.transfer.exception.RepositoryException;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by sergey on 12.10.2018.
 */

@Singleton
public class AccountDaoImpl implements AccountDao {

    private static final Logger log = Logger.getLogger(AccountDaoImpl.class.getName());


    private static final String insertQuery = "INSERT INTO Account (name, money) values (?,?)";
    private static final String deleteQuery = "DELETE FROM Account WHERE id=?";
    private static final String selectAllQuery = "SELECT * FROM Account";
    private static final String selectQuery = "SELECT * FROM Account where id=?";
    private static final String selectMoneyBlockedQuery = "SELECT money FROM Account where id=? FOR UPDATE";
    private static final String updateQuery = "UPDATE Account SET money=? WHERE id=?";


    @Inject
    private ConnectionFactory connectionFactory;

    public AccountDaoImpl() {
    }

    public AccountDaoImpl(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public Optional<AccountObj> getAccountObj(long accId) throws RepositoryException {

        AccountObj account = null;

        try (Connection conn = connectionFactory.getDBConnection();
             PreparedStatement ps = conn.prepareStatement(selectQuery)) {
            ps.setLong(1, accId);
            try (ResultSet res = ps.executeQuery()) {
                if (res.next()) {
                    long id = res.getLong("id");
                    String name = res.getString("name");
                    int money = res.getInt("money");
                    account = new AccountObj(id, name, money);
                    log.log(Level.INFO, "Account was found {0}", account);
                }
            }

        } catch (SQLException e) {
            throw new RepositoryException("Exception while retrieving account data by id", e);
        }

        return Optional.ofNullable(account);
    }

    @Override
    public List<AccountObj> getAllAccountObj() throws RepositoryException {

        List<AccountObj> accounts = new ArrayList<>();

        try (Connection conn = connectionFactory.getDBConnection();
             PreparedStatement ps = conn.prepareStatement(selectAllQuery)) {

            try (ResultSet res = ps.executeQuery()) {
                while (res.next()) {
                    long id = res.getLong("id");
                    String name = res.getString("name");
                    int money = res.getInt("money");

                    accounts.add(new AccountObj(id, name, money));
                }
                log.log(Level.INFO, "Accounts were found, amount:{0}", accounts.size());
            }

        } catch (SQLException e) {
            throw new RepositoryException("Exception while retrieving account data", e);
        }

        return accounts;
    }

    @Override
    public AccountObj createNewAccountObj(String name, int money) throws AccountException, RepositoryException {

        try (Connection conn = connectionFactory.getDBConnection();
             PreparedStatement ps = conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, name);
            ps.setInt(2, money);
            try {
                int updNum = ps.executeUpdate();
            } catch (SQLException e) {
                throw new AccountException("Account duplication", e);
            }


            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    long accountId = rs.getLong(1);
                    log.log(Level.INFO, "Account were created, id:{0}", accountId);
                    return new AccountObj(accountId, name, money);
                } else {
                    throw new RepositoryException("Account was not created: id wasnt generated");
                }
            }

        } catch (SQLException e) {
            throw new RepositoryException("Exception while creating Account", e);
        }

    }

    @Override
    public int deleteAccountObj(long accountId) throws RepositoryException {
        try (Connection conn = connectionFactory.getDBConnection();
             PreparedStatement ps = conn.prepareStatement(deleteQuery)) {

            ps.setLong(1, accountId);
            int result = ps.executeUpdate();

            log.log(Level.INFO, "Account were deleted, id:{0}, rows affected:{1}", new Object[]{accountId, result});
            return result;
        } catch (SQLException e) {
            throw new RepositoryException("Exception while deleting Account", e);
        }
    }

    @Override
    public int transferMoney(long fromId, long toId, int transferMoney) throws AccountException, RepositoryException {

        int updatedResult = 0;

        try (Connection conn = connectionFactory.getDBConnection()) {

            conn.setAutoCommit(false);

            int moneyFrom = getMoneyQueryBlocked(conn, fromId);
            int moneyTo = getMoneyQueryBlocked(conn, toId);

            if (moneyFrom >= transferMoney) {
                try (PreparedStatement updateSt = conn.prepareStatement(updateQuery)) {
                    updateSt.setInt(1, moneyTo + transferMoney);
                    updateSt.setLong(2, toId);
                    updateSt.addBatch();

                    updateSt.setInt(1, moneyFrom - transferMoney);
                    updateSt.setLong(2, fromId);
                    updateSt.addBatch();

                    int[] batchRes = updateSt.executeBatch();

                    for (int i : batchRes) {
                        updatedResult += i;
                    }
                    log.log(Level.INFO, "Accounts were updated for transfer fromId:{0}, toId:{1}, rows affected:{2}",
                            new Object[]{fromId, toId, updatedResult});

                } catch (SQLException e) {
                    log.log(Level.INFO,
                            "Exception while doing transfer, transaction should be rolled back fromId:{0}, toId:{1}",
                            new Object[]{fromId, toId});

                    conn.rollback();
                    conn.setAutoCommit(true);
                    throw e;
                }
                conn.commit();
                conn.setAutoCommit(true);
                log.log(Level.INFO, "Transfer was done successfully fromId:{0}, toId:{1}",
                        new Object[]{fromId, toId});
            } else {
                throw new AccountException("Insufficient funds on source account id: " + fromId);
            }

        } catch (SQLException e) {
            throw new RepositoryException("Exception while transferring money from account: " + fromId + " to account: " + toId, e);
        }


        return updatedResult;
    }

    private int getMoneyQueryBlocked(Connection conn, long accountId) throws SQLException, AccountException {
        try (PreparedStatement psFrom = conn.prepareStatement(selectMoneyBlockedQuery)) {
            psFrom.setLong(1, accountId);

            try (ResultSet res = psFrom.executeQuery()) {
                if (res.next()) {
                    log.log(Level.INFO, "Account row blocked for transfer id:{0}", accountId);
                    return res.getInt("money");
                } else {
                    throw new AccountException("Account was not found id: " + accountId);
                }

            }

        }
    }


    @Override
    public int addMoney(long toId, int addMoney) throws AccountException, RepositoryException {
        int updatedResult;
        try (Connection conn = connectionFactory.getDBConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement psFrom = conn.prepareStatement(selectMoneyBlockedQuery)) {
                psFrom.setLong(1, toId);
                int accountMoney;
                try (ResultSet res = psFrom.executeQuery()) {
                    if (res.next()) {
                        accountMoney = res.getInt("money");
                        log.log(Level.INFO, "Account row was blocked for update id:{0}", toId);
                    } else {
                        throw new AccountException("Account was not found id: " + toId);
                    }

                }

                try (PreparedStatement ps = conn.prepareStatement(updateQuery)) {

                    ps.setInt(1, accountMoney + addMoney);
                    ps.setLong(2, toId);

                    updatedResult = ps.executeUpdate();
                } catch (SQLException e) {
                    log.log(Level.INFO, "Exception while adding money to account id:{0}. " +
                            "Transaction should be rolled back", toId);
                    conn.rollback();
                    conn.setAutoCommit(true);
                    throw e;

                }
                conn.commit();
                conn.setAutoCommit(true);
                log.log(Level.INFO, "Deposit was added successfully id:{0}", toId);
            }


        } catch (SQLException e) {
            throw new RepositoryException("Exception while retrieving account data", e);
        }
        return updatedResult;
    }

}
