package model;


public class User {
    private int userId;
    private String username;
    private String firstName;
    private String lastName;
    private String password;
    private String email;
    private boolean adminLevel;



    public User(){

    }

    public User(String username, String first, String last, String password, String email){
        super();
        this.username = username;
        this.firstName = first;
        this.lastName= last;
        this.password = password;
        this.email= email;
    }

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
    }
}
