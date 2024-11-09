package org.acme.service;

import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.acme.exceptions.UserNotFoundException;
import org.acme.model.User;

import java.util.List;

@ApplicationScoped
public class UserService {

    public static final int MIN_PASSWORD_LENGTH = 4;

    @Inject
    EntityManager entityManager;

    public UserService() {
    }

    // Quarkus will handle the authentication process automatically based on your configuration.
    // You don't need to manually verify passwords.
    public boolean verifyPassword(String plainPassword, String hashedPassword) {
        return BcryptUtil.matches(plainPassword, hashedPassword);
    }

    public User getUserById(Long id) {
        return entityManager.find(User.class, id);
    }

    public User getUserByEmail(String email) {
        return entityManager.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                .setParameter("email", email)
                .getSingleResult();
    }

    public List<User> getAllUsers() {
        assert entityManager != null;
        return entityManager.createQuery("SELECT u FROM User u", User.class).getResultList();
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
        var hashedPassword = BcryptUtil.bcryptHash(password);
//        return BcryptUtil.matches(password, hashedPassword);
        return user.getPassword().equals(hashedPassword);
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
                password.matches(".*[A-Z].*");  // &&
//                password.matches(".*[a-z].*") &&
//                password.matches(".*\\d.*") &&
//                password.matches(".*[!@#$%^&*()].*");
    }

    public boolean emailDoesNotExist(String email) {
        return getUserByEmail(email) == null;
    }

}

