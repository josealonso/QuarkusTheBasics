package org.acme.view;

import io.quarkus.test.Mock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.InjectMock;    // IMPORTANT!
import jakarta.faces.application.FacesMessage;
import jakarta.faces.application.FacesMessage.Severity;
import jakarta.faces.context.FacesContext;
import jakarta.faces.render.RenderKit;
import jakarta.inject.Inject;
import org.acme.model.User;
import org.acme.service.EmailService;
import org.acme.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;

@QuarkusTest
public class ForgotPasswordBeanTest {

    @InjectMock
    private UserService userService;

    @InjectMock
    private EmailService emailService;

    @Inject
    ForgotPasswordBean forgotPasswordBean;

    private List<FacesMessage> messages;

    @BeforeEach
    void setUp() {
        messages = new ArrayList<>();
        // Create a custom implementation for FacesContext
        FacesContext facesContext = new FacesContext() {
            @Override
            public void addMessage(String clientId, FacesMessage message) {
                messages.add(message);
            }

            // Other required methods (can be left empty if not used in tests)
            @Override public void release() {}
            @Override public void renderResponse() {}
            @Override public void responseComplete() {}
            @Override public boolean getRenderResponse() { return false; }
            @Override public boolean getResponseComplete() { return false; }
            @Override public jakarta.faces.application.Application getApplication() { return null; }
            @Override public jakarta.faces.context.ExternalContext getExternalContext() { return null; }
            //@Override public jakarta.faces.context.FacesContext.PartialViewContext getPartialViewContext() { return null; }
            @Override public java.util.Iterator<String> getClientIdsWithMessages() { return null; }
            @Override public jakarta.faces.context.ResponseStream getResponseStream() { return null; }
            @Override public void setResponseStream(jakarta.faces.context.ResponseStream responseStream) {}
            @Override public jakarta.faces.context.ResponseWriter getResponseWriter() { return null; }
            @Override public void setResponseWriter(jakarta.faces.context.ResponseWriter responseWriter) {}
            @Override public jakarta.faces.component.UIViewRoot getViewRoot() { return null; }
            @Override public void setViewRoot(jakarta.faces.component.UIViewRoot root) {}
            @Override public jakarta.el.ELContext getELContext() { return null; }
            @Override public java.util.List<FacesMessage> getMessageList() { return messages; }
            @Override public java.util.List<FacesMessage> getMessageList(String clientId) { return messages; }
            @Override public java.util.Map<Object, Object> getAttributes() { return null; }
            @Override public jakarta.faces.context.ExceptionHandler getExceptionHandler() { return null; }
            @Override public void setExceptionHandler(jakarta.faces.context.ExceptionHandler exceptionHandler) {}
            @Override public jakarta.faces.lifecycle.Lifecycle getLifecycle() { return null; }
            // @Override public jakarta.faces.context.Flash getFlash() { return null; }
            @Override public boolean isValidationFailed() { return false; }
            @Override public void validationFailed() {}
            @Override public boolean isProjectStage(jakarta.faces.application.ProjectStage stage) { return false; }
            @Override public boolean isProcessingEvents() { return false; }
            @Override public void setProcessingEvents(boolean processingEvents) {}
            @Override public boolean isPostback() { return false; }
            @Override public void setCurrentPhaseId(jakarta.faces.event.PhaseId currentPhaseId) {}
            @Override public jakarta.faces.event.PhaseId getCurrentPhaseId() { return null; }

            @Override
            public Severity getMaximumSeverity() {
                return null;
            }

            @Override
            public Iterator<FacesMessage> getMessages() {
                return null; //throw new UnsupportedOperationException("Unimplemented method 'getMessages'");
            }

            @Override
            public Iterator<FacesMessage> getMessages(String clientId) {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'getMessages'");
            }

            @Override
            public RenderKit getRenderKit() {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'getRenderKit'");
            }
        };

        // Set our custom FacesContext as the current instance
        setFacesContextField(facesContext);
    }

    private void setFacesContextField(FacesContext facesContext) {
        try {
            // Get the setCurrentInstance method
            java.lang.reflect.Method setCurrentInstance = FacesContext.class.getDeclaredMethod("setCurrentInstance", FacesContext.class);
            // Make it accessible
            setCurrentInstance.setAccessible(true);
            // Set our custom FacesContext
            setCurrentInstance.invoke(null, facesContext);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set FacesContext", e);
        }
    }

    @Test
    void testRequestPasswordResetWithValidEmail() throws Exception {
        // Arrange
        String testEmail = "test@example.com";
        User mockUser = new User();
        mockUser.setEmail(testEmail);
        
        forgotPasswordBean.setEmail(testEmail);
        when(userService.getUserByEmail(testEmail)).thenReturn(mockUser);

        // Act
        String result = forgotPasswordBean.requestPasswordReset();

        // Assert
        verify(emailService, times(1)).sendHtmlEmail(
            anyString(),  // email
            anyString(),  // subject
            anyString()   // content
        );
        
        // Verify that a message was added
        assert !messages.isEmpty() : "Expected FacesMessage to be added";
        FacesMessage message = messages.get(0);
        assert message.getSeverity() == FacesMessage.SEVERITY_INFO : "Expected INFO severity message";
    }

    @Test
    void testRequestPasswordResetWithInvalidEmail() throws Exception {
        // Arrange
        String testEmail = "nonexistent@example.com";
        forgotPasswordBean.setEmail(testEmail);
        
        when(userService.getUserByEmail(testEmail)).thenReturn(null);

        // Act
        String result = forgotPasswordBean.requestPasswordReset();

        // Assert
        verify(emailService, never()).sendHtmlEmail(
            anyString(),  // email
            anyString(),  // subject
            anyString()   // content
        );
        
        // Verify that a message was added - Note: For security reasons, we show the same INFO message
        // even when the email doesn't exist
        assert !messages.isEmpty() : "Expected FacesMessage to be added";
        FacesMessage message = messages.get(0);
        assert message.getSeverity() == FacesMessage.SEVERITY_INFO : "Expected INFO severity message";
        assert message.getSummary().equals("Success") : "Expected 'Success' message summary";
        assert message.getDetail().equals("If an account exists with this email, you will receive password reset instructions.") 
            : "Expected generic message for security";
    }

    @Test
    void testRequestPasswordResetWithException() throws Exception {
        // Arrange
        String testEmail = "test@example.com";
        forgotPasswordBean.setEmail(testEmail);
        
        when(userService.getUserByEmail(testEmail)).thenThrow(new RuntimeException("Database error"));

        // Act
        String result = forgotPasswordBean.requestPasswordReset();

        // Assert
        verify(emailService, never()).sendHtmlEmail(
            anyString(),  // email
            anyString(),  // subject
            anyString()   // content
        );
        
        // Verify that an error message was added
        assert !messages.isEmpty() : "Expected FacesMessage to be added";
        FacesMessage message = messages.get(0);
        assert message.getSeverity() == FacesMessage.SEVERITY_ERROR : "Expected ERROR severity message";
        assert message.getSummary().equals("Error") : "Expected 'Error' message summary";
        assert message.getDetail().equals("An error occurred while processing your request.") 
            : "Expected error message";
    }
}
