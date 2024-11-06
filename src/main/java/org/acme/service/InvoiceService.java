package org.acme.service;

import jakarta.enterprise.context.RequestScoped;
import org.acme.controller.Invoice;

import java.util.List;

@RequestScoped
public class InvoiceService {
    public List<Invoice> getUserInvoices() {
        return List.of(
                new Invoice(0, "1", "2023-06-01", "John Doe", "100.00"),
                new Invoice(1, "2", "2023-06-02", "Jane Doe", "200.00"),
                new Invoice(2, "3", "2023-06-03", "Bob Smith", "300.00"),
                new Invoice(3, "4", "2023-06-04", "Alice Johnson", "400.00")
        );
    }

    public void deleteInvoice(Long id) {
    }
}
