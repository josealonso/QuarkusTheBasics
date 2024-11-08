package org.acme.service;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.Map;

@ApplicationScoped
public class UserService {

//    @Inject
//    EntityManager entityManager;
    Map<String, String> users;

    public UserService() {
        users = Map.of(
                "admin", "admin", "user", "user",
                "guest", "guest", "jose", "jose"
        );
    }

    public boolean authenticate(String username, String password) {
        if (password == null || !users.containsKey(username) || !users.get(username).equals(password)) {
            return false; 
        }
        return password.equals(users.get(username));
    }

    public boolean registerUser(String username, String password, String email) {
        return false;
    }
}
