package org.acme.view;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.acme.exceptions.UserNotFoundException;
import org.acme.model.User;
import org.acme.service.UserService;

import static java.lang.Thread.sleep;

@Named
@RequestScoped
public class RegistrationBean {
    private Long id;
    private String username;
    private String password;
    private String email;
    private String roles;

    @Inject
    private UserService userService;

    public String register() throws UserNotFoundException, InterruptedException {
        try {
            // Check if email already exists
            // if (userService.getUserByEmail(email) != null) {
            // FacesContext.getCurrentInstance().addMessage(null,
            // new FacesMessage(FacesMessage.SEVERITY_ERROR, "Email already exists", null));
            // return null;
            // }

            // Create new user
            id = 5L;
            roles = "user";
            User newUser = new User();
            newUser.setId(id);
            newUser.setUsername(username);
            newUser.setPassword(password);
            newUser.setEmail(email);
            newUser.setRole(roles);

            userService.createUser(newUser);
            sleep(1000);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Registration successful",
                            "You can now log in with your new credentials."));

            clearForm();

            // Redirect to login page
            return "login.xhtml?faces-redirect=true";
        } catch (UserNotFoundException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Registration failed", null));
            return null;
        }
    }

    private void clearForm() {
        username = null;
        password = null;
        email = null;
    }

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public String login() {
        return "/login.xhtml?faces-redirect=true";
    }
}
