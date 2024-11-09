package org.acme.model;

import io.quarkus.security.jpa.Password;
import io.quarkus.security.jpa.Roles;
import io.quarkus.security.jpa.UserDefinition;
import io.quarkus.security.jpa.Username;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@UserDefinition
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "email", unique = true, nullable = false)
    @Username
    private String email;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "password", nullable = false)
    @Password
    private String password;  // This will store the bcrypt hashed password

    @Column(name = "roles")
    @Roles
    private String role;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "user_invoices",
            joinColumns = @JoinColumn(name = "invoice_id")
//            inverseJoinColumns = @JoinColumn(name = "follower_id")
    )
    private Set<User> userInvoices = new HashSet<>();

//    @ManyToMany(mappedBy = "invoices")
//    private Set<Invoice> userInvoices = new HashSet<>();

    // Default constructor
    public User() {
    }

    public User(Long id, String email, String username, String password, String role) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.password = password;
        this.role = role;
    }
    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
    // Helper methods for managing relationships

//    public void addFollower(User follower) {
//        followers.add(follower);
//        follower.getFollowing().add(this);
//    }
//
//    public void removeFollower(User follower) {
//        followers.remove(follower);
//        follower.getFollowing().remove(this);
//    }

    // Equals and hashCode methods

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return getId() != null && getId().equals(user.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
