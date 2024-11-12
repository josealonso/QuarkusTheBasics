import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.acme.service.UserService;

import static java.lang.Thread.sleep;

import java.nio.file.Files;

@Named
@RequestScoped
public class LoginBean {
    private String username;
    private String password;
    private String email;
    private boolean rememberMe;

    ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();

    @Inject
    private UserService userService;

    public String login() throws Exception {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Logging in", null));
        sleep(1000);
        writeLogs("FFFFFFFFFFF - Going to call getUserByEmail with EMAIL: " + email);
        // System.out.println(" ====A=A=A=A=== About to call getUserByEmail with EMAIL:
        // " + email);
        var user = userService.getUserByEmail(email);
        writeLogs("user: " + user);
        if (user != null) {
            userService.isTheRightPassword(user, password);
            externalContext.getSessionMap().put("user", user);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Login successful", null));
            return "listing?faces-redirect=true";
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid credentials", null));
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

    private void writeLogs(String message) throws Exception {
        Files.writeString(java.nio.file.Path.of("logs.txt"), message
                + "\n");
    }
}
