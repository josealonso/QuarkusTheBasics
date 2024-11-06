package org.acme.service;

import jakarta.enterprise.context.RequestScoped;
import org.acme.controller.Invoice;

import java.util.List;

@RequestScoped
public class InvoiceService {
    public List<Invoice> getUserInvoices() {
        return List.of(
                Invoice.builder().invoiceNumber("1").invoiceDate("2023-06-01").customerName("John Doe").amount("100.00").build(),
                Invoice.builder().invoiceNumber("2").invoiceDate("2023-06-02").customerName("Jane Doe").amount("200.00").build()
        );
    }

    public void deleteInvoice(Long id) {
    }
}
