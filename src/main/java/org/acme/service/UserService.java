package org.acme.service;

import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.acme.model.User;

@ApplicationScoped
public class UserService {

    public static final int MIN_PASSWORD_LENGTH = 8;
    @Inject
    EntityManager entityManager;

//    Map<String, String> users;

    public UserService() {
//        users = Map.of(
//                "admin", "admin", "user", "user",
//                "guest", "guest", "jose", "jose"
//        );
    }

    // Quarkus will handle the authentication process automatically based on your configuration.
    // You don't need to manually verify passwords.
    public boolean verifyPassword(String plainPassword, String hashedPassword) {
        return BcryptUtil.matches(plainPassword, hashedPassword);
    }

    @Transactional
    public boolean addUser(String username, String password, String role) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(BcryptUtil.bcryptHash(password));
        user.setRole(role);
        entityManager.persist(user);
        return true;
    }

    public boolean authenticate(String username, String password) {
        var hashedPassword = entityManager.createQuery("SELECT u.password FROM User u WHERE u.username = :username",
                        String.class)
                .setParameter("username", username)
                .getSingleResult();
        return verifyPassword(password, hashedPassword);
    }


    public void changePassword(String username, String newPassword) {
        User user = entityManager.find(User.class, "username");
        if (user != null) {
            user.setPassword(BcryptUtil.bcryptHash(newPassword));
            entityManager.persist(user);
        }
    }

    public boolean isPasswordValid(String password) {
        return password.length() >= MIN_PASSWORD_LENGTH &&
                password.matches(".*[A-Z].*") &&
                password.matches(".*[a-z].*") &&
                password.matches(".*\\d.*") &&
                password.matches(".*[!@#$%^&*()].*");
    }

}

