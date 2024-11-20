package org.acme.view;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.acme.service.UserService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@Named
@RequestScoped
public class LoginBean {
    private String username;
    private String password;
    private String email;
    private boolean rememberMe;

    private ExternalContext externalContext;

    @Inject
    private UserService userService;

    private static boolean isFirstWrite = true;

    private ExternalContext getExternalContext() {
        if (externalContext == null) {
            externalContext = FacesContext.getCurrentInstance().getExternalContext();
        }
        return externalContext;
    }

    public void checkLoggedIn() {
        if (getExternalContext().getSessionMap().get("user") == null) {
            try {
                getExternalContext().redirect(getExternalContext().getRequestContextPath() + "/login.xhtml");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String login() throws Exception {
        var context = FacesContext.getCurrentInstance();
        try {
            writeLogs("Login attempt with email: " + email);
            var user = userService.getUserByEmail(email);
            writeLogs("Found user: " + (user != null ? user.getUsername() : "null"));
            
            if (user != null) {
                boolean passwordCorrect = userService.isTheRightPassword(user, password);
                writeLogs("Password check result: " + passwordCorrect);
                
                if (passwordCorrect) {
                    getExternalContext().getSessionMap().put("user", user);
                    context.addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_INFO, "Login successful", null));
                    return "listing?faces-redirect=true";
                }
            }
            
            context.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid credentials", null));
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            writeLogs("Login error: " + e.getMessage());
            context.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error occurred during login", null));
            return null;
        }
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public boolean isRememberMe() {
        return rememberMe;
    }

    public void setRememberMe(boolean rememberMe) {
        this.rememberMe = rememberMe;
    }

    private void writeLogs(String text) throws Exception {
        System.out.println("LOG: " + text);
        if (isFirstWrite) {
            Files.writeString(Paths.get("LoginBean-logs.txt"), text + "\n", StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            isFirstWrite = false;
        } else {
            Files.writeString(Paths.get("LoginBean-logs.txt"), text + "\n", StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        }
    }

    public String logout() {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "/login.xhtml?faces-redirect=true";
    }
}
