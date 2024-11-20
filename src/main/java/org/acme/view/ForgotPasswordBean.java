package org.acme.view;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.acme.service.UserService;
import org.acme.service.EmailService;
import java.util.UUID;

@Named
@RequestScoped
public class ForgotPasswordBean {
    
    private String recipientEmail;
    
    @Inject
    private UserService userService;
    
    @Inject
    private EmailService emailService;

    public String getEmail() {
        return recipientEmail;
    }

    public void setEmail(String email) {
        this.recipientEmail = email;
    }
    
    public String requestPasswordReset() throws Exception {
        try {
            var user = userService.getUserByEmail(recipientEmail);
            if (user != null) {
                // Generate a reset token
                String resetToken = UUID.randomUUID().toString();
                
                // TODO: Save the token to the database with an expiration time
                // userService.saveResetToken(user.getId(), resetToken, expirationTime);
                
                // Create the reset link
                String resetLink = "http://localhost:8080/resetPassword.xhtml?token=" + resetToken;
                
                // Create HTML email content
                String htmlContent = formEmailContent(resetLink);
                
                // Send the email
                emailService.sendHtmlEmail(
                    recipientEmail,
                    "Password Reset Request",
                    htmlContent
                );
                
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

    private String formEmailContent(String resetLink) {
      
        return """
                    <html>
                        <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                            <h2>Password Reset Request</h2>
                            <p>Hello,</p>
                            <p>We received a request to reset your password. Click the link below to set a new password:</p>
                            <p>
                                <a href="%s" 
                                   style="background-color: #007bff; 
                                          color: white; 
                                          padding: 10px 20px; 
                                          text-decoration: none; 
                                          border-radius: 5px; 
                                          display: inline-block;">
                                    Reset Password
                                </a>
                            </p>
                            <p>If you didn't request this, you can safely ignore this email.</p>
                            <p>This link will expire in 24 hours.</p>
                            <p>Best regards,<br>Your Application Team</p>
                        </body>
                    </html>
                    """.formatted(resetLink);
    }
}
