package com.example.transfer.dto;

import com.example.transfer.dao.model.AccountObj;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * Created by sergey on 12.10.2018.
 */
public class Account {
    private long id;
    @NotNull
    @Pattern(regexp = "^[a-zA-Z ]+$", message = "Incorrect name")
    private String name;
    @Min(value = 0, message = "Initial balance cant be negative")
    private int money;

    public Account() {
    }

    public Account(String name, int money) {
        this.name = name;
        this.money = money;
    }

    public Account(AccountObj accountObj) {
        this.id = accountObj.getId();
        this.name = accountObj.getName();
        this.money = accountObj.getMoney();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

        Account account = (Account) o;

        if (id != account.id) return false;
        if (money != account.money) return false;
        return name != null ? name.equals(account.name) : account.name == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + money;
        return result;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", money=" + money +
                '}';
    }
}
