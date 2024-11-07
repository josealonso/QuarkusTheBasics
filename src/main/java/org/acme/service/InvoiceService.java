package org.acme.service;

import io.quarkus.hibernate.orm.PersistenceUnit;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.acme.controller.InvoiceDTO;
import org.acme.exceptions.InvoiceNotFoundException;
import org.acme.model.Invoice;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class InvoiceService {

    private List<InvoiceDTO> userInvoices;

    @Inject
    @PersistenceUnit("invoices")
    EntityManager entityManager;

    public InvoiceService() {
//        init();
    }

    @Transactional
    public InvoiceDTO createInvoice(InvoiceDTO invoiceDTO) {
        writeLogs("DDDDDDDDDDD - Creating invoice: " + invoiceDTO);
        entityManager.persist(convertFromInvoiceDTO(invoiceDTO));
        return invoiceDTO;
    }

    public Optional<InvoiceDTO> getInvoiceById(Long id) throws InvoiceNotFoundException {
        if (entityManager.find(Invoice.class, id) == null) {
            throw new InvoiceNotFoundException("Invoice with ID " + id + " not found");
        }
        return Optional.of(convertToInvoiceDTO(entityManager.find(Invoice.class, id)));
    }

    public List<InvoiceDTO> getAllInvoices() {
        return entityManager.createQuery("SELECT i FROM Invoice i", Invoice.class).getResultList()
                .stream().map(this::convertToInvoiceDTO).toList();
    }

    @Transactional
    public InvoiceDTO updateInvoice(InvoiceDTO invoiceDTO) {
        Invoice invoice = convertFromInvoiceDTO(invoiceDTO);
        return convertToInvoiceDTO(entityManager.merge(invoice));
    }

    @Transactional
    public void deleteInvoice(Long id) throws InvoiceNotFoundException {
        writeLogs("DDDDDDDDDDD - Deleting invoice with ID: " + id);
        var invoiceDTO = getInvoiceById(id).get();
        entityManager.remove(convertFromInvoiceDTO(invoiceDTO));
    }

    public List<Invoice> getInvoicesByStatus(String status) {
        return entityManager.createQuery("SELECT i FROM Invoice i WHERE i.status = :status", Invoice.class)
                .setParameter("status", status)
                .getResultList();
    }

    /******************** DTO to/from ENTITY CONVERSION METHODS **********************/

    private InvoiceDTO convertToInvoiceDTO(Invoice invoice) {
        return new InvoiceDTO(invoice.getId(), invoice.getInvoiceNumber(), invoice.getInvoiceDate(),
                invoice.getCustomerName(), invoice.getAmount());
    }

    private Invoice convertFromInvoiceDTO(InvoiceDTO invoiceDTO) {
        return new Invoice(invoiceDTO.getId(), invoiceDTO.getInvoiceNumber(), invoiceDTO.getInvoiceDate(),
                invoiceDTO.getCustomerName(), invoiceDTO.getAmount());
    }

    private void writeLogs(String text) {
        try {
            Files.writeString(java.nio.file.Path.of("logs.txt"), text + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}



