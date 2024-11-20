package org.acme.view;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.acme.model.User;
import org.acme.service.EmailService;
import org.acme.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import io.quarkus.test.InjectMock;
import io.quarkus.test.Mock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@QuarkusTest
public class ForgotPasswordBeanTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ForgotPasswordBeanTest.class);

    @InjectMock
    private UserService userService;

    @InjectMock
    private EmailService emailService;

    @Inject
    ForgotPasswordBean forgotPasswordBean;

    private FacesContext facesContext;
    @InjectMock
    private ExternalContext externalContext;

    private List<FacesMessage> messages;
    private Map<String, Object> sessionMap;
    private MockedStatic<FacesContext> mockedStatic;

    @BeforeEach
    void setUp() throws Exception {
        LOGGER.debug("Setting up test...");
        messages = new ArrayList<FacesMessage>();
        sessionMap = new HashMap<>();
        
        // Initialize FacesContext mock
        facesContext = mock(FacesContext.class);
        mockedStatic = mockStatic(FacesContext.class);
        
        LOGGER.debug("Setting up external context...");
        // Setup external context
        mockedStatic.when(FacesContext::getCurrentInstance).thenReturn(facesContext);
        when(facesContext.getExternalContext()).thenReturn(externalContext);
        when(externalContext.getSessionMap()).thenReturn(sessionMap);
        
        LOGGER.debug("Setting up message handling...");
        // Setup message handling
        doAnswer(invocation -> {
            String clientId = invocation.getArgument(0);
            FacesMessage message = invocation.getArgument(1);
            LOGGER.debug("Adding message - ClientId: {}, Severity: {}, Summary: {}, Detail: {}", 
                clientId, message.getSeverity(), message.getSummary(), message.getDetail());
            messages.add(message);
            return null;
        }).when(facesContext).addMessage(anyString(), any(FacesMessage.class));

        LOGGER.debug("Setting up FacesContext current instance...");
        // Verify the setup
        FacesContext currentInstance = FacesContext.getCurrentInstance();
        LOGGER.debug("Current FacesContext instance: {}", currentInstance);
        if (currentInstance == null) {
            LOGGER.error("FacesContext.getCurrentInstance() is null!");
        } else {
            LOGGER.debug("FacesContext.getCurrentInstance() successfully set");
        }
        LOGGER.debug("Test setup completed");
    }

    @AfterEach
    void tearDown() throws Exception {
        if (mockedStatic != null) {
            mockedStatic.close();
        }
        // Reset the ThreadLocal instance
        Field threadLocalField = FacesContext.class.getDeclaredField("currentInstance");
        threadLocalField.setAccessible(true);
        threadLocalField.set(null, null);
        messages.clear();
    }

    @Test
    void testRequestPasswordResetWithValidEmail() throws Exception {
        // Verify FacesContext before test
        LOGGER.info("FacesContext before test: {}", FacesContext.getCurrentInstance());
        
        // Arrange
        String testEmail = "test@example.com";
        User mockUser = new User();
        mockUser.setEmail(testEmail);
        
        forgotPasswordBean.setEmail(testEmail);
        when(userService.getUserByEmail(testEmail)).thenReturn(mockUser);

        // Act
        LOGGER.debug("Executing requestPasswordReset()");
        String result = forgotPasswordBean.requestPasswordReset();
        LOGGER.debug("requestPasswordReset() completed with result: {}", result);

        // Assert
        LOGGER.debug("Verifying email service calls");
        verify(emailService, times(1)).sendHtmlEmail(
            anyString(),  // email
            anyString(),  // subject
            anyString()   // content
        );
        
        LOGGER.debug("Messages size after test: {}", messages.size());
        LOGGER.debug("Messages content: {}", messages.stream()
            .map(m -> String.format("[Severity: %s, Summary: %s, Detail: %s]", 
                m.getSeverity(), m.getSummary(), m.getDetail()))
            .collect(java.util.stream.Collectors.joining(", ")));
        // Verify that a message was added
        assertThat(messages).isEmpty();
        FacesMessage message = messages.get(0);
        assertThat(message.getSeverity()).isEqualTo(FacesMessage.SEVERITY_INFO);
        assertThat(message.getSummary()).isEqualTo("Success");
        assertThat(message.getDetail()).isEqualTo("If an account exists with this email, you will receive password reset instructions.");
    }

    @Test
    void testRequestPasswordResetWithInvalidEmail() throws Exception {
        LOGGER.debug("Starting testRequestPasswordResetWithInvalidEmail");
        
        // Verify FacesContext before test
        FacesContext currentInstance = FacesContext.getCurrentInstance();
        LOGGER.debug("FacesContext before test: {}", currentInstance);
        if (currentInstance == null) {
            LOGGER.error("FacesContext is null before test!");
        }
        
        // Arrange
        String testEmail = "nonexistent@example.com";
        forgotPasswordBean.setEmail(testEmail);
        
        when(userService.getUserByEmail(testEmail)).thenReturn(null);

        // Act
        LOGGER.debug("Executing requestPasswordReset()");
        String result = forgotPasswordBean.requestPasswordReset();
        LOGGER.debug("requestPasswordReset() completed with result: {}", result);

        // Assert
        LOGGER.debug("Verifying email service calls");
        verify(emailService, never()).sendHtmlEmail(
            anyString(),  // email
            anyString(),  // subject
            anyString()   // content
        );
        
        // Verify that a message was added - Note: For security reasons, we show the same INFO message
        // even when the email doesn't exist
        assertThat(messages).isNotEmpty();  // First check if messages exist
        FacesMessage message = messages.get(0);
        assertThat(message.getSeverity()).isEqualTo(FacesMessage.SEVERITY_INFO);
        assertThat(message.getSummary()).isEqualTo("Success");
        assertThat(message.getDetail()).isEqualTo("If an account exists with this email, you will receive password reset instructions.");
    }

    @Test
    void testRequestPasswordResetWithException() throws Exception {
        // Verify FacesContext before test
        LOGGER.info("FacesContext before test: {}", FacesContext.getCurrentInstance());
        
        // Arrange
        String testEmail = "test@example.com";
        forgotPasswordBean.setEmail(testEmail);
        
        when(userService.getUserByEmail(testEmail)).thenThrow(new RuntimeException("Database error"));

        // Act
        LOGGER.debug("Executing requestPasswordReset()");
        String result = forgotPasswordBean.requestPasswordReset();
        LOGGER.debug("requestPasswordReset() completed with result: {}", result);

        // Assert
        LOGGER.debug("Verifying email service calls");
        verify(emailService, never()).sendHtmlEmail(
            anyString(),  // email
            anyString(),  // subject
            anyString()   // content
        );
        
        // Verify that an error message was added
        assertThat(messages).isNotEmpty();  // First check if messages exist
        FacesMessage message = messages.get(0);
        assertThat(message.getSeverity()).isEqualTo(FacesMessage.SEVERITY_ERROR);
        assertThat(message.getSummary()).isEqualTo("Error");
        assertThat(message.getDetail()).isEqualTo("An error occurred while processing your request.");
    }
}
