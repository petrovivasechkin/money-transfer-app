package com.example.transfer.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * Created by sergey on 13.10.2018.
 */
public class AccountTransfer {
    @NotNull
    private long fromAccountId;
    @NotNull
    private long toAccountId;
    @Min(1)
    private int money;

    public AccountTransfer() {
    }

    public AccountTransfer(long fromAccountId, long toAccountId, int money) {
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.money = money;
    }

    public long getFromAccountId() {
        return fromAccountId;
    }

    public void setFromAccountId(long fromAccountId) {
        this.fromAccountId = fromAccountId;
    }

    public long getToAccountId() {
        return toAccountId;
    }

    public void setToAccountId(long toAccountId) {
        this.toAccountId = toAccountId;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AccountTransfer transfer = (AccountTransfer) o;

        if (fromAccountId != transfer.fromAccountId) return false;
        if (toAccountId != transfer.toAccountId) return false;
        return money == transfer.money;
    }

    @Override
    public int hashCode() {
        int result = (int) (fromAccountId ^ (fromAccountId >>> 32));
        result = 31 * result + (int) (toAccountId ^ (toAccountId >>> 32));
        result = 31 * result + money;
        return result;
    }

    @Override
    public String toString() {
        return "AccountTransfer{" +
                "fromAccountId=" + fromAccountId +
                ", toAccountId=" + toAccountId +
                ", money=" + money +
                '}';
    }
}
