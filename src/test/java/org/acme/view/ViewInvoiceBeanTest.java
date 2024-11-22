package org.acme.view;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Optional;

import org.acme.controller.InvoiceDTO;
import org.acme.exceptions.InvoiceNotFoundException;
import org.acme.service.InvoiceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;

class ViewInvoiceBeanTest {

    @Mock
    private InvoiceService invoiceService;

    @Mock
    private FacesContext facesContext;

    @Mock
    private ExternalContext externalContext;

    private ViewInvoiceBean viewInvoiceBean;
    private InvoiceDTO testInvoice;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Create test invoice
        testInvoice = new InvoiceDTO(1L, "100.00", "Test Customer", "2024-01-01", "INV001");
        
        // Setup external context
        when(facesContext.getExternalContext()).thenReturn(externalContext);
        
        // Create bean instance
        viewInvoiceBean = new ViewInvoiceBean();
        setField(viewInvoiceBean, "invoiceService", invoiceService);
        setField(viewInvoiceBean, "facesContext", facesContext);
        setField(viewInvoiceBean, "externalContext", externalContext);
    }

    @Test
    void testInitSuccess() throws InvoiceNotFoundException {
        // Setup
        viewInvoiceBean.setInvoiceId(1L);
        when(invoiceService.getInvoiceById(1L)).thenReturn(Optional.of(testInvoice));

        // Execute
        viewInvoiceBean.init();

        // Verify
        assertThat(viewInvoiceBean.getInvoice()).isEqualTo(testInvoice);
    }

    @Test
    void testInitInvoiceNotFound() throws InvoiceNotFoundException {
        // Setup
        viewInvoiceBean.setInvoiceId(1L);
        when(invoiceService.getInvoiceById(1L)).thenThrow(new InvoiceNotFoundException("Invoice not found"));

        // Execute
        viewInvoiceBean.init();

        // Verify error message
        ArgumentCaptor<FacesMessage> messageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
        verify(facesContext).addMessage(eq(null), messageCaptor.capture());
        
        FacesMessage capturedMessage = messageCaptor.getValue();
        assertThat(capturedMessage.getSeverity()).isEqualTo(FacesMessage.SEVERITY_ERROR);
        assertThat(capturedMessage.getSummary()).isEqualTo("Error");
        assertThat(capturedMessage.getDetail()).isEqualTo("Invoice not found");

        // Verify navigation
        try {
            verify(externalContext).redirect("listing.xhtml");
        } catch (IOException e) {
            // This shouldn't happen in the test
            throw new RuntimeException(e);
        }
    }

    @Test
    void testDeleteInvoiceSuccess() throws InvoiceNotFoundException {
        // Setup
        viewInvoiceBean.setInvoice(testInvoice);

        // Execute
        viewInvoiceBean.deleteInvoice();

        // Verify
        verify(invoiceService).deleteInvoice(testInvoice.getInvoiceNumber().toString());

        // Verify success message
        ArgumentCaptor<FacesMessage> messageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
        verify(facesContext).addMessage(eq(null), messageCaptor.capture());
        
        FacesMessage capturedMessage = messageCaptor.getValue();
        assertThat(capturedMessage.getSeverity()).isEqualTo(FacesMessage.SEVERITY_INFO);
        assertThat(capturedMessage.getSummary()).isEqualTo("Success");
        assertThat(capturedMessage.getDetail()).isEqualTo("Invoice deleted successfully");

        // Verify navigation
        try {
            verify(externalContext).redirect("listing.xhtml");
        } catch (IOException e) {
            // This shouldn't happen in the test
            throw new RuntimeException(e);
        }
    }

    @Test
    void testDeleteInvoiceFailure() throws InvoiceNotFoundException {
        // Setup
        viewInvoiceBean.setInvoice(testInvoice);
        doThrow(new RuntimeException("Delete failed"))
            .when(invoiceService).deleteInvoice(testInvoice.getInvoiceNumber().toString());

        // Execute
        viewInvoiceBean.deleteInvoice();

        // Verify error message
        ArgumentCaptor<FacesMessage> messageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
        verify(facesContext).addMessage(eq(null), messageCaptor.capture());
        
        FacesMessage capturedMessage = messageCaptor.getValue();
        assertThat(capturedMessage.getSeverity()).isEqualTo(FacesMessage.SEVERITY_ERROR);
        assertThat(capturedMessage.getSummary()).isEqualTo("Error");
        assertThat(capturedMessage.getDetail()).isEqualTo("Failed to delete invoice");
    }

    @Test
    void testDownloadPdf() {
        // Execute
        viewInvoiceBean.downloadPdf();

        // Verify info message (since it's not implemented yet)
        ArgumentCaptor<FacesMessage> messageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
        verify(facesContext).addMessage(eq(null), messageCaptor.capture());
        
        FacesMessage capturedMessage = messageCaptor.getValue();
        assertThat(capturedMessage.getSeverity()).isEqualTo(FacesMessage.SEVERITY_INFO);
        assertThat(capturedMessage.getSummary()).isEqualTo("Info");
        assertThat(capturedMessage.getDetail()).isEqualTo("PDF download feature coming soon");
    }

    // Utility method to set private fields using reflection
    private void setField(Object target, String fieldName, Object value) {
        try {
            java.lang.reflect.Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
