package org.acme.view;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.acme.service.UserService;

@Named
@RequestScoped
public class ForgotPasswordBean {
    
    private String email;
    
    @Inject
    private UserService userService;
    
    public String requestPasswordReset() {
        try {
            var user = userService.getUserByEmail(email);
            if (user != null) {
                // In a real application, you would:
                // 1. Generate a reset token
                // 2. Save it to the database with an expiration
                // 3. Send an email with a reset link
                
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, 
                        "Success", 
                        "If an account exists with this email, you will receive password reset instructions."));
            } else {
                // For security reasons, show the same message even if user doesn't exist
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, 
                        "Success", 
                        "If an account exists with this email, you will receive password reset instructions."));
            }
            return null;
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                    "Error", 
                    "An error occurred while processing your request."));
            return null;
        }
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
}
