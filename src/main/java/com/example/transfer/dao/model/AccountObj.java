package com.example.transfer.dao.model;

/**
 * Created by sergey on 12.10.2018.
 */
public class AccountObj {
    private long id;

    private String name;

    private int money;

    public AccountObj(long id, String name, int money) {
        this.id = id;
        this.name = name;
        this.money = money;
    }

    public AccountObj(String name, int money) {
        this.name = name;
        this.money = money;
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

        AccountObj that = (AccountObj) o;

        if (id != that.id) return false;
        if (money != that.money) return false;
        return name != null ? name.equals(that.name) : that.name == null;
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
        return "AccountObj{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", money=" + money +
                '}';
    }
}
