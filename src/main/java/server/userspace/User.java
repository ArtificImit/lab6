package server.userspace;

import server.util.Hash;

public class User {
    private String username;
    private String passwordHash;

    public User(String username, String passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public boolean verifyPassword(String password) {
        return Hash.hashMD2(password).equals(passwordHash);
    }
}
