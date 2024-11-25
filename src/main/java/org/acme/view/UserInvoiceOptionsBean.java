package org.acme.view;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.acme.Utilities;
import org.acme.controller.InvoiceDTO;
import org.acme.model.Invoice;
import org.acme.model.User;
import org.acme.service.InvoiceService;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

    private User registeredUser;

    @Inject
    private InvoiceService invoiceService;

    @Inject
    private FacesContext facesContext;

    @Inject
    private ExternalContext externalContext;

    private static final String API_BASE_URL = "/api/invoices";

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

    public User getRegisteredUser() {
        return registeredUser;
    }

    /* The @Transactional annotation ensures we have an active session when accessing the invoices. 
     * We only load the invoices once and cache them in the bean.
     */
    @Transactional
    public List<InvoiceDTO> getUserInvoices() {
        if (userInvoices == null) {
            // userInvoices = registeredUser.getInvoices().stream()
            userInvoices = registeredUser.getInvoices().stream()
                .map((Invoice invoice) -> invoiceService.convertToInvoiceDTO(invoice))
                .collect(Collectors.toList());
        }
        return userInvoices;
    }

    @PostConstruct
    public void init() {
        ExternalContext externalContext = facesContext.getExternalContext();
        Map<String, Object> sessionMap = externalContext.getSessionMap();
        registeredUser = (User) sessionMap.get("user");
        userInvoices = invoiceService.getInvoicesByUser(registeredUser);
    }

    public String viewInvoice(Long id) {
        Utilities.writeToCentralLog("Inside UserInvoiceOptionsBean.viewInvoice() with ID: " + id);
        
        // Check if user is in session
        User currentUser = (User) facesContext.getExternalContext().getSessionMap().get("user");
        if (currentUser == null) {
            Utilities.writeToCentralLog("No user in session during navigation attempt");
            facesContext.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Please log in to view invoices"));
            return "login.xhtml?faces-redirect=true";
        }
        
        String redirectUrl = "viewInvoice.xhtml?faces-redirect=true&id=" + id;
        Utilities.writeToCentralLog("Redirecting to: " + redirectUrl + " for user: " + currentUser.getEmail());
        
        facesContext.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Viewing invoice", "Invoice ID: " + id));
        return redirectUrl;
    }

    public void deleteInvoice(Long invoiceNumber) {
        try {
            writeLogs("Deleting invoice with number: " + invoiceNumber);
            invoiceService.deleteInvoice(invoiceNumber);
            
            // Refresh the invoice list
            //userInvoices = null; // Force reload of the list
            userInvoices = invoiceService.getInvoicesByUser(registeredUser);
            
            facesContext.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Invoice deleted successfully"));
                
        } catch (Exception e) {
            String errorMsg = "Failed to delete invoice: " + e.getMessage();
            writeLogs("Error: " + errorMsg);
            facesContext.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", errorMsg));
        }
    }

    public String goToCreateInvoice() {
        // Navigate to a page where there is a form to create the invoice
        return "newInvoice.xhtml?faces-redirect=true";
    }

    public String viewInvoice(InvoiceDTO invoice) {
        if (invoice == null || invoice.getId() == null) {
            return null;
        }
        return "viewInvoice.xhtml?faces-redirect=true&id=" + invoice.getId();
    }

    public String editInvoice(Long invoiceId) {
        writeLogs("Inside editInvoice with invoiceId: " + invoiceId);
        return "editInvoice?faces-redirect=true&id=" + invoiceId;
    }

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
