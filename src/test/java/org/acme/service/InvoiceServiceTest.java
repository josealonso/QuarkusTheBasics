package org.acme.service;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.InjectMock;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.NoResultException;
import org.acme.exceptions.InvoiceNotFoundException;
import org.acme.model.Invoice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import jakarta.inject.Inject;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@QuarkusTest
public class InvoiceServiceTest {

    @Inject
    InvoiceService invoiceService;

    @InjectMock
    EntityManager entityManager;

    @BeforeEach
    void setUp() {
        Mockito.clearInvocations(entityManager);
    }

    @Test
    void deleteInvoiceSuccess() throws InvoiceNotFoundException {
        // Arrange
        String invoiceNumber = "INV001";
        Invoice mockInvoice = new Invoice();
        mockInvoice.setInvoiceNumber(invoiceNumber);

        TypedQuery<Invoice> typedQuery = mock(TypedQuery.class);
        when(entityManager.createQuery(anyString(), eq(Invoice.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter(eq("invoiceNumber"), any())).thenReturn(typedQuery);
        when(typedQuery.getSingleResult()).thenReturn(mockInvoice);

        // Act & Assert
        assertThatCode(() -> 
            invoiceService.deleteInvoice(invoiceNumber)
        ).doesNotThrowAnyException();

        verify(entityManager).remove(mockInvoice);
    }

    @Test
    void deleteInvoiceNotFound() {
        // Arrange
        String invoiceNumber = "NON_EXISTENT";
        
        TypedQuery<Invoice> typedQuery = mock(TypedQuery.class);
        when(entityManager.createQuery(anyString(), eq(Invoice.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter(eq("invoiceNumber"), any())).thenReturn(typedQuery);
        when(typedQuery.getSingleResult()).thenThrow(new NoResultException("No invoice found"));

        // Act & Assert
        assertThatThrownBy(() -> 
            invoiceService.deleteInvoice(invoiceNumber)
        )
        .isInstanceOf(InvoiceNotFoundException.class)
        .hasMessage("Invoice with number NON_EXISTENT not found");
        
        verify(entityManager, never()).remove(any());
    }
}
