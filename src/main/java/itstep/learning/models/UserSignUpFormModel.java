package itstep.learning.models;

import java.util.ArrayList;
import java.util.Date;

public class UserSignUpFormModel {
    private String name;
    private String email;
    private ArrayList<String> extraEmails;
    private String phone;
    private String password;
    private Date dob;
    private String city;
    private String login;

    public UserSignUpFormModel() {
        this.extraEmails = new ArrayList<>();
    }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public void setName(String name) { this.name = name; }
    public String getName() { return name; }

    public void setEmail(String email) { this.email = email; }
    public String getEmail() { return email; }

    public void setPassword(String password) { this.password = password; }
    public String getPassword() { return password; }

    public void setDob(Date dob) { this.dob = dob; }
    public Date getDob() { return dob; }

    public ArrayList<String> getExtraEmails() { return extraEmails; }
    public void setExtraEmails(ArrayList<String> extraEmails) { this.extraEmails = extraEmails; }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
