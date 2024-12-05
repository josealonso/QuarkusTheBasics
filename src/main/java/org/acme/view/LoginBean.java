package org.acme.view;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;
import org.acme.service.UserService;
import org.acme.Utilities;
import org.acme.config.ResourceBundleConfig;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ResourceBundle;

@Named
@SessionScoped
public class LoginBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private String username;
    private String password;
    private String email;
    private boolean rememberMe;

    private ExternalContext externalContext;

    @Inject
    private UserService userService;

    @Inject
    private ResourceBundleConfig resourceBundleConfig;

    private static boolean isFirstWrite = true;

    private ExternalContext getExternalContext() {
        if (externalContext == null) {
            externalContext = FacesContext.getCurrentInstance().getExternalContext();
        }
        return externalContext;
    }

    @PostConstruct
    public void init() {
        try {
            if (resourceBundleConfig != null) {
                var bundle = resourceBundleConfig.getResourceBundle();
                if (bundle != null) {
                    String welcomeMessage = bundle.getString("login.welcome");
                    Utilities.writeToCentralLog(welcomeMessage);
                }
            }
        } catch (Exception e) {
            Utilities.writeToCentralLog("Error initializing LoginBean: " + e.getMessage());
        }
    }

    public void checkLoggedIn() {
        try {
            ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
            if (externalContext.getRequest() instanceof HttpServletRequest) {
                if (externalContext.getSessionMap().get("user") == null) {
                    externalContext.redirect(externalContext.getRequestContextPath() + "/login.xhtml");
                }
            }
        } catch (IOException e) {
            Utilities.writeToCentralLog("Error in checkLoggedIn: " + e.getMessage());
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

    public String logout() {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "/login.xhtml?faces-redirect=true";
    }

    public String register() {
        return "/registration.xhtml?faces-redirect=true";
    }

    public String forgotPassword() {
        return "/forgotPassword.xhtml?faces-redirect=true";
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

}
