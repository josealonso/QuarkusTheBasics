package org.acme.view;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.acme.model.User;
import org.acme.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;

@QuarkusTest
public class LoginBeanTest {

    @InjectMock
    private UserService userService;

    @Inject
    LoginBean loginBean;

    private FacesContext facesContext;
    private ExternalContext externalContext;
    private List<FacesMessage> messages;
    private Map<String, Object> sessionMap;
    private MockedStatic<FacesContext> mockedStatic;

    @BeforeEach
    void setUp() {
        messages = new ArrayList<>();
        sessionMap = new HashMap<>();
        
        // Initialize FacesContext mock
        facesContext = mock(FacesContext.class);
        externalContext = mock(ExternalContext.class);
        mockedStatic = mockStatic(FacesContext.class);
        
        // Setup external context
        mockedStatic.when(FacesContext::getCurrentInstance).thenReturn(facesContext);
        when(facesContext.getExternalContext()).thenReturn(externalContext);
        when(externalContext.getSessionMap()).thenReturn(sessionMap);
        
        // Setup message handling
        doAnswer(invocation -> {
            String clientId = invocation.getArgument(0);
            FacesMessage message = invocation.getArgument(1);
            messages.add(message);
            return null;
        }).when(facesContext).addMessage(anyString(), any(FacesMessage.class));
    }

    @AfterEach
    void tearDown() {
        if (mockedStatic != null) {
            mockedStatic.close();
        }
    }

    @Test
    void testSuccessfulLogin() throws Exception {
        // Arrange
        String email = "test@example.com";
        String password = "password123";
        User mockUser = new User();
        mockUser.setEmail(email);
        mockUser.setUsername("testuser");

        when(userService.getUserByEmail(email)).thenReturn(mockUser);
        when(userService.isTheRightPassword(mockUser, password)).thenReturn(true);

        loginBean.setEmail(email);
        loginBean.setPassword(password);

        // Act
        String result = loginBean.login();

        // Assert
        assertThat(result).isEqualTo("listing?faces-redirect=true");
        assertThat(sessionMap).containsKey("user");
        assertThat(sessionMap.get("user")).isEqualTo(mockUser);
        // TODO
       /* assertThat(messages).hasSize(1);
        assertThat(messages.get(0).getSeverity()).isEqualTo(FacesMessage.SEVERITY_INFO);
        assertThat(messages.get(0).getSummary()).isEqualTo("Login successful"); */
    }

    @Test
    void testLoginWithInvalidCredentials() throws Exception {
        // Arrange
        String email = "test@example.com";
        String password = "wrongpassword";
        User mockUser = new User();
        mockUser.setEmail(email);

        when(userService.getUserByEmail(email)).thenReturn(mockUser);
        when(userService.isTheRightPassword(mockUser, password)).thenReturn(false);

        loginBean.setEmail(email);
        loginBean.setPassword(password);

        // Act
        String result = loginBean.login();

        // Assert
        assertThat(result).isNull();
        assertThat(sessionMap).doesNotContainKey("user");
        // TODO
       /* assertThat(messages).hasSize(1);
        assertThat(messages.get(0).getSeverity()).isEqualTo(FacesMessage.SEVERITY_ERROR);
        assertThat(messages.get(0).getSummary()).isEqualTo("Invalid credentials"); */
    }

    @Test
    void testLoginWithNonexistentUser() throws Exception {
        // Arrange
        String email = "nonexistent@example.com";
        String password = "password123";

        when(userService.getUserByEmail(email)).thenReturn(null);

        loginBean.setEmail(email);
        loginBean.setPassword(password);

        // Act
        String result = loginBean.login();

        // Assert
        assertThat(result).isNull();
        assertThat(sessionMap).doesNotContainKey("user");
        // TODO
        /* assertThat(messages).hasSize(1);
        assertThat(messages.get(0).getSeverity()).isEqualTo(FacesMessage.SEVERITY_ERROR);
        assertThat(messages.get(0).getSummary()).isEqualTo("Invalid credentials"); */
    }

    @Test
    void testLoginWithException() throws Exception {
        // Arrange
        String email = "test@example.com";
        String password = "password123";

        when(userService.getUserByEmail(email)).thenThrow(new RuntimeException("Database error"));

        loginBean.setEmail(email);
        loginBean.setPassword(password);

        // Act
        String result = loginBean.login();

        // Assert
        assertThat(result).isNull();
        assertThat(sessionMap).doesNotContainKey("user");
        // TODO
        /* assertThat(messages).hasSize(1);
        assertThat(messages.get(0).getSeverity()).isEqualTo(FacesMessage.SEVERITY_ERROR);
        assertThat(messages.get(0).getSummary()).isEqualTo("An error occurred during login"); */
    }

    @Test
    void testCheckLoggedInWhenNotLoggedIn() throws Exception {
        // Arrange
        when(externalContext.getRequestContextPath()).thenReturn("/app");
        doNothing().when(externalContext).redirect(anyString());

        // Act
        loginBean.checkLoggedIn();

        // Assert
        verify(externalContext).redirect("/app/login.xhtml");
    }

    @Test
    void testCheckLoggedInWhenLoggedIn() throws Exception {
        // Arrange
        sessionMap.put("user", new User());

        // Act
        loginBean.checkLoggedIn();

        // Assert
        verify(externalContext, never()).redirect(anyString());
    }
}
