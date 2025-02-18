package itstep.learning.dal.dto;

import java.util.UUID;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;


public class User {

    private UUID userId;
    private String name;
    private String phone;
    private String city;
    private Date dofb;
    private int age;
    private double money;
    private String email;


    public static User froResulSet(ResultSet rs) throws SQLException {

        User user = new User();

        user.setUserId(UUID.fromString(rs.getString("userId")));
        user.setName(rs.getString("name"));
        user.setPhone(rs.getString("phone"));
        user.setCity(rs.getString("city"));
        user.setDofb(rs.getDate("dateOfB"));
        user.setAge(rs.getInt("age"));
        user.setMoney(rs.getFloat("money"));
        user.setEmail(rs.getString("email"));

        return user;

    }

    public Date getDofb() {
        return dofb;
    }


    public void setDofb(Date dofb) {
        this.dofb = dofb;
    }
    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

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

}
