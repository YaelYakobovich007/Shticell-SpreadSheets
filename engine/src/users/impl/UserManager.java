package users.impl;

import java.util.*;

public class UserManager {
    private final Set<String> users;

    public UserManager() {
        users = new HashSet<>();
    }

    public synchronized void addUser(String username) {
        users.add(username);
    }

    public synchronized void removeUser(String username) {
        users.remove(username);
    }

    public synchronized Set<String> getUsers() {
        return users;
    }

    public boolean isUserExists(String username) {
        return users.contains(username);
    }
}
