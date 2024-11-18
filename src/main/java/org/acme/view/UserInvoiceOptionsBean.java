package org.acme.view;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.transaction.Transactional;
import org.acme.controller.InvoiceDTO;
import org.acme.model.Invoice;
import org.acme.model.User;
import org.acme.service.InvoiceService;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Named("userInvoiceOptionsBean")
@ViewScoped
public class UserInvoiceOptionsBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<InvoiceDTO> userInvoices;
    private int firstIndex;
    private int rowsPerPage = 5;
    private int startRecord;
    private int endRecord;
    private int totalRecords;

    private User currentUser;

    @Inject
    private InvoiceService invoiceService;

    @Inject
    private FacesContext facesContext;

    @Inject
    public UserInvoiceOptionsBean(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    public void setUserInvoices(List<InvoiceDTO> userInvoices) {
        this.userInvoices = userInvoices;
    }

    public void setFirstIndex(int firstIndex) {
        this.firstIndex = firstIndex;
    }

    public void setRowsPerPage(int rowsPerPage) {
        this.rowsPerPage = rowsPerPage;
    }

    public int getRowsPerPage() {
        return rowsPerPage;
    }

    public int getStartRecord() {
        return firstIndex + 1;
    }

    public void setStartRecord(int startRecord) {
        this.startRecord = startRecord;
    }

    public void setEndRecord(int endRecord) {
        this.endRecord = endRecord;
    }

    public int getEndRecord() {
        int end = firstIndex + getRowsPerPage();
        int total = userInvoices.size();
        return Math.min(end, total);
    }

    public int getTotalRecords() {
        return userInvoices.size();
    }

    public User getCurrentUser() {
        return currentUser;
    }

    /* The @Transactional annotation ensures we have an active session when accessing the invoices. 
     * We only load the invoices once and cache them in the bean.
     */
    @Transactional
    public List<InvoiceDTO> getUserInvoices() {
        if (userInvoices == null) {
            userInvoices = currentUser.getInvoices().stream()
                .map((Invoice invoice) -> invoiceService.convertToInvoiceDTO(invoice))
                .collect(Collectors.toList());
        }
        return userInvoices;
    }

    @PostConstruct
    public void init() {
        ExternalContext externalContext = facesContext.getExternalContext();
        Map<String, Object> sessionMap = externalContext.getSessionMap();
        currentUser = (User) sessionMap.get("user");
        userInvoices = invoiceService.getInvoicesByUser(currentUser);
    }

    public String viewInvoice(Long id) {
        // Logic to view an invoice
        // This could navigate to a new page or open a dialog
        facesContext.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Viewing invoice", "Invoice ID: " + id));
        return "viewInvoice.xhtml?faces-redirect=true&id=" + id;
    }

    // TODO - Bug: This method is only called for the first deletion, not for
    // subsequent ones.
    // This should be fixed.
    public void deleteInvoice(Long id) {
        try {
            writeLogs("FFFFFFFFFFF - Going to delete invoice with ID: " + id);
            invoiceService.deleteInvoice(id);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Invoice deleted successfully"));
            // getUserInvoices();
        } catch (Exception e) {
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to delete invoice"));
        }
    }

    public String prepareNewInvoice() {
        // Navigate to a page where there is a form to create the invoice
        return "newInvoice.xhtml?faces-redirect=true";
    }

    private void writeLogs(String text) {
        try {
            Files.writeString(java.nio.file.Path.of("logs.txt"), text + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
