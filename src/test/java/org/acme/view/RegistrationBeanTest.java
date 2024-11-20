package org.acme.view;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.acme.exceptions.UserNotFoundException;
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
public class RegistrationBeanTest {

    @InjectMock
    private UserService userService;

    @Inject
    RegistrationBean registrationBean;

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
        
        // Setup mock behavior
        mockedStatic.when(FacesContext::getCurrentInstance).thenReturn(facesContext);
        when(facesContext.getExternalContext()).thenReturn(externalContext);
        when(externalContext.getSessionMap()).thenReturn(sessionMap);
        
        // Setup addMessage capture
        doAnswer(invocation -> {
            FacesMessage message = invocation.getArgument(1);
            messages.add(message);
            return null;
        }).when(facesContext).addMessage(anyString(), any(FacesMessage.class));
    }

    @AfterEach
    void tearDown() {
        mockedStatic.close();
    }

    @Test
    void testSuccessfulRegistration() throws UserNotFoundException, InterruptedException {
        // Setup test data
        User mockedUser = new User(1L,"testUser", "testPass", "test@example.com", "user");
        registrationBean.setUsername(mockedUser.getUsername());
        registrationBean.setPassword(mockedUser.getPassword());
        registrationBean.setEmail(mockedUser.getEmail());
        
        // Setup mock behavior
        when(userService.createUser(any(User.class))).thenReturn(mockedUser);
        
        // Execute registration
        String result = registrationBean.register();
        
        // Verify redirects to login page
        assertThat(result).isEqualTo("login.xhtml?faces-redirect=true");
        
        // Verify user creation was called
        verify(userService).createUser(any(User.class));
        
        // TODO
        // Verify success message was added
        /* assertThat(messages).hasSize(1);
        assertThat(messages.get(0).getSeverity()).isEqualTo(FacesMessage.SEVERITY_INFO);
        assertThat(messages.get(0).getSummary()).isEqualTo("Registration successful"); */
        
        // Verify form was cleared
        assertThat(registrationBean.getUsername()).isNull();
        assertThat(registrationBean.getPassword()).isNull();
        assertThat(registrationBean.getEmail()).isNull();
    }

    @Test
    void testFailedRegistration() throws UserNotFoundException, InterruptedException {
        // Setup test data
        User mockedUser = new User(1L,"testUser", "testPass", "test@example.com", "user");
        registrationBean.setUsername("testUser");
        registrationBean.setPassword("testPass");
        registrationBean.setEmail("test@example.com");
        
        // Setup mock to throw exception
        doThrow(new UserNotFoundException("Registration failed")).when(userService).createUser(any(User.class));
        
        // Execute registration
        String result = registrationBean.register();
        
        // Verify no redirect
        assertThat(result).isNull();

        // TODO
        // Verify error message was added
        /*assertThat(messages).hasSize(1);
        assertThat(messages.get(0).getSeverity()).isEqualTo(FacesMessage.SEVERITY_ERROR);
        assertThat(messages.get(0).getSummary()).isEqualTo("Registration failed"); */
    }

    @Test
    void testGettersAndSetters() {
        // Test ID
        Long testId = 1L;
        registrationBean.setId(testId);
        assertThat(registrationBean.getId()).isEqualTo(testId);

        // Test Username
        String testUsername = "testUser";
        registrationBean.setUsername(testUsername);
        assertThat(registrationBean.getUsername()).isEqualTo(testUsername);

        // Test Password
        String testPassword = "testPass";
        registrationBean.setPassword(testPassword);
        assertThat(registrationBean.getPassword()).isEqualTo(testPassword);

        // Test Email
        String testEmail = "test@example.com";
        registrationBean.setEmail(testEmail);
        assertThat(registrationBean.getEmail()).isEqualTo(testEmail);

        // Test Roles
        String testRoles = "user";
        registrationBean.setRoles(testRoles);
        assertThat(registrationBean.getRoles()).isEqualTo(testRoles);
    }
}
