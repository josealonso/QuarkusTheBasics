package org.acme.view;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.acme.controller.InvoiceDTO;
import org.acme.exceptions.InvoiceNotFoundException;
import org.acme.model.User;
import org.acme.service.InvoiceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;

class UserInvoiceOptionsBeanTest {

    @Mock
    private InvoiceService invoiceService;

    @Mock
    private FacesContext facesContext;

    @Mock
    private ExternalContext externalContext;

    private UserInvoiceOptionsBean userInvoiceOptionsBean;
    private User testUser;
    private Map<String, Object> sessionMap;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Create test user
        testUser = new User(1L, "alice@gmail.com", "testuser", "password", "USER");
        testUser.setInvoices(new ArrayList<>());
        
        // Setup session map
        sessionMap = new HashMap<>();
        sessionMap.put("user", testUser);
        
        // Mock external context
        when(facesContext.getExternalContext()).thenReturn(externalContext);
        when(externalContext.getSessionMap()).thenReturn(sessionMap);
        
        // Create bean instance
        userInvoiceOptionsBean = new UserInvoiceOptionsBean(invoiceService);
        // Inject mocked FacesContext
        setField(userInvoiceOptionsBean, "facesContext", facesContext);
        // Set the registered user - IMPORTANT!!
        setField(userInvoiceOptionsBean, "registeredUser", testUser);
    }

    @Test
    void testInit() {
        // Setup
        List<InvoiceDTO> mockInvoices = List.of(
            new InvoiceDTO(1L, "100.00", "Customer 1", "2024-01-01", "INV001")
        );
        when(invoiceService.getInvoicesByUser(testUser)).thenReturn(mockInvoices);

        // Execute
        userInvoiceOptionsBean.init();

        // Verify
        assertThat(userInvoiceOptionsBean.getUserInvoices()).isEqualTo(mockInvoices);
        verify(invoiceService).getInvoicesByUser(testUser);
    }

    @Test
    void testDeleteInvoiceSuccess() throws InvoiceNotFoundException {
        // Setup
        InvoiceDTO invoiceDTO = new InvoiceDTO(1L, "100.00", "Customer 1", "2024-01-01", "INV001");
        List<InvoiceDTO> updatedInvoices = new ArrayList<>();
        when(invoiceService.getInvoicesByUser(testUser)).thenReturn(updatedInvoices);

        // Execute
        userInvoiceOptionsBean.deleteInvoice(invoiceDTO.getInvoiceNumber().toString());

        // Verify
        verify(invoiceService).deleteInvoice(invoiceDTO.getInvoiceNumber().toString());
        verify(invoiceService).getInvoicesByUser(any(User.class));
        
        // Verify success message
        ArgumentCaptor<FacesMessage> messageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
        verify(facesContext).addMessage(eq(null), messageCaptor.capture());
        FacesMessage capturedMessage = messageCaptor.getValue();
        assertThat(capturedMessage.getSeverity()).isEqualTo(FacesMessage.SEVERITY_INFO);
        assertThat(capturedMessage.getSummary()).isEqualTo("Success");
    }

    @Test
    void testDeleteInvoiceFailure() throws InvoiceNotFoundException {
        // Setup
        String invoiceNumber = "INV001";
        doThrow(new RuntimeException("Delete failed")).when(invoiceService).deleteInvoice(invoiceNumber);

        // Execute
        userInvoiceOptionsBean.deleteInvoice(invoiceNumber);

        // Verify error message
        ArgumentCaptor<FacesMessage> messageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
        verify(facesContext).addMessage(eq(null), messageCaptor.capture());
        FacesMessage capturedMessage = messageCaptor.getValue();
        assertThat(capturedMessage.getSeverity()).isEqualTo(FacesMessage.SEVERITY_ERROR);
        assertThat(capturedMessage.getSummary()).isEqualTo("Error");
    }

    @Test
    void testGoToCreateInvoice() {
        String result = userInvoiceOptionsBean.goToCreateInvoice();
        assertThat(result).isEqualTo("newInvoice.xhtml?faces-redirect=true");
    }

    // Utility method to set private fields using reflection
    private void setField(Object target, String fieldName, Object value) {
        try {
            java.lang.reflect.Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set field: " + fieldName, e);
        }
    }
}
