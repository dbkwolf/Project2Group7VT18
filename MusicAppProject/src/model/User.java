package model;


import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class User {
    private SimpleIntegerProperty userId;
    private SimpleStringProperty strUserId;
    private SimpleStringProperty username;
    private SimpleStringProperty firstName;
    private SimpleStringProperty lastName;
    private SimpleStringProperty password;
    private SimpleStringProperty email;
    private SimpleBooleanProperty adminLevel;
    private SimpleStringProperty strAdminLevel;




    public User(int id, String username, String first, String last, String password, String email, boolean isadmin){
        super();

        this.userId = new SimpleIntegerProperty(id);
        this.strUserId = new SimpleStringProperty(Integer.toString(id));
        this.username = new SimpleStringProperty(username);
        this.firstName = new SimpleStringProperty(first);
        this.lastName= new SimpleStringProperty(last);
        this.password = new SimpleStringProperty(password);
        this.email= new SimpleStringProperty(email);
        this.adminLevel = new SimpleBooleanProperty(isadmin);

        if (isadmin){
            this.strAdminLevel = new SimpleStringProperty("admin");
        }else{
            this.strAdminLevel = new SimpleStringProperty("standard");
        }
    }



    public String getUsername() {
        return username.get();
    }

    public SimpleStringProperty usernameProperty() {
        return username;
    }

    public void setUsername(String username) {
        this.username.set(username);
    }

    public String getFirstName() {
        return firstName.get();
    }

    public SimpleStringProperty firstNameProperty() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName.set(firstName);
    }

    public String getLastName() {
        return lastName.get();
    }

    public SimpleStringProperty lastNameProperty() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName.set(lastName);
    }

    public String getPassword() {
        return password.get();
    }

    public SimpleStringProperty passwordProperty() {
        return password;
    }

    public void setPassword(String password) {
        this.password.set(password);
    }

    public String getEmail() {
        return email.get();
    }

    public SimpleStringProperty emailProperty() {
        return email;
    }

    public void setEmail(String email) {
        this.email.set(email);
    }

    public boolean isAdminLevel() {
        return adminLevel.get();
    }

    public SimpleBooleanProperty adminLevelProperty() {
        return adminLevel;
    }

    public void setAdminLevel(boolean adminLevel) {
        this.adminLevel.set(adminLevel);
    }

    public int getUserId() {
        return userId.get();
    }

    public SimpleIntegerProperty userIdProperty() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId.set(userId);
    }

    public String getStrAdminLevel() {
        return strAdminLevel.get();
    }

    public SimpleStringProperty strAdminLevelProperty() {
        return strAdminLevel;
    }

    public void setStrAdminLevel(String strAdminLevel) {
        this.strAdminLevel.set(strAdminLevel);
    }

    public String getStrUserId() {
        return strUserId.get();
    }

    public SimpleStringProperty strUserIdProperty() {
        return strUserId;
    }

    public void setStrUserId(String strUserId) {
        this.strUserId.set(strUserId);
    }
/*
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public boolean isAdminLevel() {
        return adminLevel;
    }

    public void setAdminLevel(boolean adminLevel) {
        this.adminLevel = adminLevel;
    }*/
}
