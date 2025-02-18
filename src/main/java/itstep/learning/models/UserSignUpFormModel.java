package itstep.learning.models;

import java.util.ArrayList;
import java.util.Date;


import java.util.ArrayList;
import java.util.Date;

public class UserSignUpFormModel {

    private String name;
    private String phone;
    private String city;
    private Date dofb;
    private int age;
    private Double money;
    private String email;
    private ArrayList<ExtraEmail> extraEmail;
    private String password;
    private String login;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Date getDofb() {
        return dofb;
    }

    public void setDofb(Date dofb) {
        this.dofb = dofb;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ArrayList<ExtraEmail> getExtraEmail() {
        return extraEmail;
    }

    public void setExtraEmail(ArrayList<ExtraEmail> extraEmail) {
        this.extraEmail = extraEmail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

}
