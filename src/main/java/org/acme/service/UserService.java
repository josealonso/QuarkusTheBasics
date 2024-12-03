package org.acme.service;

import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.acme.exceptions.UserNotFoundException;
import org.acme.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

import jakarta.inject.Inject;

@ApplicationScoped
public class UserService {

    public static final int MIN_PASSWORD_LENGTH = 4;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Inject
    private EntityManager entityManager;

    public UserService() {
    }

    // Quarkus will handle the authentication process automatically based on your
    // configuration.
    // You don't need to manually verify passwords.
    public boolean verifyPassword(String plainPassword, String hashedPassword) {
        return BcryptUtil.matches(plainPassword, hashedPassword);
    }

    public User getUserById(Long id) {
        return entityManager.find(User.class, id);
    }

    public User getUserByEmail(String email) throws Exception {
        writeLogs(" ====A=A=A=A=== EMAIL: " + email);
        var users = getAllUsers();
        writeLogs("USERS: " + users);
        try {
            return entityManager.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<User> getAllUsers() {
        assert entityManager != null : "EntityManager is null";

        try {
            TypedQuery<User> query = entityManager.createQuery("SELECT u FROM User u", User.class);
            List<User> users = query.getResultList();

            if (users.isEmpty()) {
                logger.warn("No users found in the database.");
            } else {
                logger.info("Found {} users in the database.", users.size());
            }

            return users;
        } catch (Exception e) {
            logger.error("Error while fetching all users", e);
            throw new RuntimeException("Failed to fetch users", e);
        }
        // assert entityManager != null;
        // return entityManager.createQuery("SELECT u FROM User u",
        // User.class).getResultList();
    }

    @Transactional
    public User createUser(User newUser) throws UserNotFoundException {
        if (isPasswordValid(newUser.getPassword()) && emailDoesNotExist(newUser.getEmail())) {
            User user = new User();
            user.setId(newUser.getId());
            user.setUsername(newUser.getUsername());
            user.setPassword(BcryptUtil.bcryptHash(newUser.getPassword()));
            user.setEmail(newUser.getEmail());
            user.setRole(newUser.getRole());
            entityManager.persist(user);
            return user;
        } else {
            throw new UserNotFoundException("User not found");
        }
    }

    @Transactional
    public void createUser(long id, String admin, String admin1, String admin2, String admin3) {
        var newUse = new User(id, admin, admin1, admin2, admin3);
        entityManager.persist(newUse);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = entityManager.find(User.class, id);
        if (user != null) {
            entityManager.remove(user);
        }
    }

    public User authenticate(String username, String email, String password) {
        return entityManager.createQuery("SELECT u.password FROM User u WHERE u.username = :username",
                User.class)
                .setParameter("username", username)
                .getSingleResult();
        // verifyPassword(password, password);
    }

    public boolean isTheRightPassword(User user, String password) {
        // return true;  // Temporarily return true
        try {
            writeLogs("Attempting password verification:");
            writeLogs("Input password: " + password);
            writeLogs("Stored hash: " + user.getPassword());
            boolean matches = BcryptUtil.matches(password, user.getPassword());
            writeLogs("Password match result: " + matches);
            return matches;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
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
                password.matches(".*[A-Z].*"); // &&
        // password.matches(".*[a-z].*") &&
        // password.matches(".*\\d.*") &&
        // password.matches(".*[!@#$%^&*()].*");
    }

    public boolean emailDoesNotExist(String email) {
        return true;
        // return getUserByEmail(email) == null;
    }

    public void writeLogs(String text) throws Exception {
        System.out.println("LOG: " + text);  // Print to console
        Files.writeString(Paths.get("UserService-logs.txt"), text + "\n", StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    // Test method to generate BCrypt hash
    public String generateBcryptHash(String password) {
        String hash = BcryptUtil.bcryptHash(password);
        try {
            writeLogs("Generated hash for testing:");
            writeLogs("Password: " + password);
            writeLogs("Generated Hash: " + hash);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hash;
    }
}
