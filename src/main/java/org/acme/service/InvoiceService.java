package org.acme.service;

import jakarta.enterprise.context.RequestScoped;
import org.acme.controller.Invoice;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@RequestScoped
public class InvoiceService {

    private List<Invoice> userInvoices;

    public InvoiceService() {
        init();
    }

    public void init() {
        userInvoices = new ArrayList<>();
        userInvoices.add(new Invoice(0L, "1", "2023-06-01", "John Doe", "100.00"));
        userInvoices.add(new Invoice(1L, "2", "2023-06-02", "Jane Doe", "200.00"));
        userInvoices.add(new Invoice(2L, "3", "2023-06-03", "Bob Smith", "300.00"));
    }

    public List<Invoice> getUserInvoices() {
        return userInvoices;
    }

    public void deleteInvoice(Long id) {
        writeLogs("DDDDDDDDDDD - Deleting invoice with ID: " + id);
        userInvoices.removeIf(invoice -> invoice.getId().equals(id));
    }

    private void writeLogs(String text) {
        try {
            Files.writeString(java.nio.file.Path.of("logs.txt"), text + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
