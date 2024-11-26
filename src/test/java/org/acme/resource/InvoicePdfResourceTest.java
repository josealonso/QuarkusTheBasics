package org.acme.resource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Map;

import org.acme.controller.InvoiceDTO;
import org.acme.model.User;
import org.acme.service.InvoiceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.ws.rs.core.Response;

@ExtendWith(MockitoExtension.class)
public class InvoicePdfResourceTest {

    @InjectMocks
    private InvoicePdfResource invoicePdfResource;

    @Mock
    private InvoiceService invoiceService;

    @Mock
    private FacesContext facesContext;

    @Mock
    private ExternalContext externalContext;

    @BeforeEach
    void setUp() {
        when(facesContext.getExternalContext()).thenReturn(externalContext);
        when(externalContext.getSessionMap()).thenReturn(Map.of("user", new User()));
    }

    @Test
    void testCreateInvoice() {
        // given
        var invoiceDTO = new InvoiceDTO(null, "100.00", "Customer 1", 
                                    LocalDate.parse("2024-01-01"), "INV001");
        
        when(invoiceService.createInvoice(invoiceDTO)).thenReturn(invoiceDTO);
        
        // when
        var response = invoicePdfResource.createInvoice(invoiceDTO);
        
        // then
        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, String> entity = (Map<String, String>) response.getEntity();
        assertThat(entity).containsEntry("message", "Invoice created successfully");
    }
}
