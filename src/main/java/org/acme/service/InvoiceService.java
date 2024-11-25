package org.acme.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.acme.controller.InvoiceDTO;
import org.acme.exceptions.InvoiceNotFoundException;
import org.acme.model.Invoice;
import org.acme.model.User;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class InvoiceService {

    @Inject
    // @PersistenceContext(unitName = "invoices") // This annotation causes an
    // exception.
    EntityManager entityManager;

    public InvoiceService() {
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
        // writeLogs("DDDDDDDDDDD - Fetching all invoices");
        assert entityManager != null;
        // writeLogs("DDDDDDDDDDD - entityManager is not null");
        return entityManager.createQuery("SELECT i FROM Invoice i", Invoice.class).getResultList()
                .stream().map(this::convertToInvoiceDTO).toList();
    }

    // Assume the user id always exists
    public List<InvoiceDTO> getInvoicesByUser(User user) {
        try {
   /*         writeLogs("Getting invoices for user: " + user);
            
            // First check if the invoices table exists and has data
            var nativeQuery = entityManager.createNativeQuery("SELECT COUNT(*) FROM invoices");
            var totalCount = ((Number) nativeQuery.getSingleResult()).longValue();
            writeLogs("Total invoices in database: " + totalCount);
            
            // Check invoices for this specific user
            var userInvoicesCount = entityManager.createNativeQuery(
                "SELECT COUNT(*) FROM invoices WHERE user_id = ?1")
                .setParameter(1, user.getId())
                .getSingleResult();
            writeLogs("Total invoices for user " + user.getId() + ": " + userInvoicesCount);
            
            // List all user_ids in invoices table
            var userIds = entityManager.createNativeQuery(
                "SELECT DISTINCT user_id FROM invoices ORDER BY user_id")
                .getResultList();
            writeLogs("All user_ids in invoices table: " + userIds);
            */
            
            var query = "SELECT i FROM Invoice i WHERE i.user.id = :userId";
            writeLogs("JPA Query: " + query);
            writeLogs("User ID: " + user.getId()); 
            
            List<Invoice> invoices = entityManager.createQuery(query, Invoice.class)
                    .setParameter("userId", user.getId())
                    .getResultList();
            
            writeLogs("Found " + invoices.size() + " invoices");
            return invoices.isEmpty() ? Collections.emptyList() :
                    invoices.stream().map(this::convertToInvoiceDTO).toList();
        } catch (Exception e) {
            writeLogs("Error getting invoices: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    @Transactional
    public InvoiceDTO updateInvoice(InvoiceDTO invoiceDTO) {
        writeLogs("Starting to update invoice: " + invoiceDTO);
        Invoice invoice = convertFromInvoiceDTO(invoiceDTO);
        Invoice updatedInvoice = entityManager.merge(invoice);
        InvoiceDTO result = convertToInvoiceDTO(updatedInvoice);
        writeLogs("Invoice updated successfully: " + result);
        return result;
    }

    @Transactional
    public void deleteInvoice(Long id) throws InvoiceNotFoundException {
        writeLogs("Deleting invoice with ID: " + id);
        
        try {
            Invoice invoiceToBeRemoved = entityManager.find(Invoice.class, id);
                
            if (invoiceToBeRemoved == null) {
                writeLogs("Invoice not found with ID: " + id);
                throw new InvoiceNotFoundException("Invoice with ID " + id + " not found");
            }
            
            entityManager.remove(invoiceToBeRemoved);
            writeLogs("Invoice removed successfully");
            
        } catch (Exception e) {
            writeLogs("Error deleting invoice: " + e.getMessage());
            throw e;
        }
    }

    public List<Invoice> getInvoicesByStatus(String status) {
        return entityManager.createQuery("SELECT i FROM Invoice i WHERE i.status = :status", Invoice.class)
                .setParameter("status", status)
                .getResultList();
    }

    /********************
     * DTO to/from ENTITY CONVERSION METHODS
     **********************/

    public InvoiceDTO convertToInvoiceDTO(Invoice invoice) {
        return new InvoiceDTO(invoice.getId(), invoice.getAmount(), invoice.getCustomerName(),
                invoice.getInvoiceDate(), invoice.getInvoiceNumber());
    }

    public Invoice convertFromInvoiceDTO(InvoiceDTO invoiceDTO) {
        Invoice existingInvoice = entityManager.find(Invoice.class, invoiceDTO.getId());
        if (existingInvoice != null) {
            // Update existing invoice
            existingInvoice.setInvoiceNumber(invoiceDTO.getInvoiceNumber());
            existingInvoice.setInvoiceDate(invoiceDTO.getInvoiceDate());
            existingInvoice.setCustomerName(invoiceDTO.getInvoiceCustomerName());
            existingInvoice.setAmount(invoiceDTO.getInvoiceAmount());
            return existingInvoice;
        } else {
            // Create new invoice
            return new Invoice(invoiceDTO.getId(), 
                             invoiceDTO.getInvoiceNumber(), 
                             invoiceDTO.getInvoiceDate(),
                             invoiceDTO.getInvoiceCustomerName(), 
                             invoiceDTO.getInvoiceAmount(), 
                             new User());
        }
    }

    /************************
     * Temporary LOGGING METHODS
     *********************************************/
    private void writeLogs(String text) {
        try {
            Files.writeString(Path.of("logs.txt"), text + "\n", 
                StandardOpenOption.CREATE, 
                StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
