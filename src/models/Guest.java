package models;

public class Guest {
    private String username;
    private String password;

    public Guest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() { return this.username; }
    public String getPassword() { return this.password; }
}
