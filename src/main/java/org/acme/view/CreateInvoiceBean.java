package org.acme.view;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.ws.rs.core.Response;

import org.acme.Utilities;
import org.acme.controller.InvoiceDTO;
import org.acme.model.User;
import org.acme.resource.InvoicePdfResource;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named("createInvoiceBean")
@ViewScoped
public class CreateInvoiceBean implements Serializable {
    private static final Logger logger = Logger.getLogger(CreateInvoiceBean.class.getName());
    private static final long serialVersionUID = 1L;

    @Inject
    private FacesContext facesContext;

    @Inject
    private ExternalContext externalContext;

    @Inject
    private InvoicePdfResource invoicePdfResource;

    private User sessionUser;

    @NotEmpty(message = "Invoice Number cannot be empty")
    @Pattern(regexp = "^\\d+$", message = "Invoice number must contain only digits")
    private String invoiceNumber;

    @NotNull(message = "Invoice Date cannot be empty")
    private LocalDate invoiceDate;

    @NotEmpty(message = "Customer name cannot be empty")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Customer name must contain only letters and spaces")
    private String customerName;

    @NotEmpty(message = "Invoice Amount cannot be empty")
    @Pattern(regexp = "^[0-9]+(\\.[0-9]{1,2})?$", message = "Amount must be a number with up to two decimal places")
    private String amount;

    @PostConstruct
    public void init() {
        try {
            // Get session user
            Map<String, Object> sessionMap = externalContext.getSessionMap();
            sessionUser = (User) sessionMap.get("user");

            if (sessionUser == null) {
                facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No user in session"));
                navigateToLogin();
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to initialize CreateInvoiceBean", e);
            facesContext.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to initialize: " + e.getMessage()));
        }
    }

    public String submitInvoice() {
        try {
            logger.info("Creating new invoice");
            writeToLog("Inside CreateInvoiceBean.submitInvoice(). \n"
            + "Creating new invoice with these data: \nInvoice number: " + invoiceNumber + "\nInvoice Date: " + invoiceDate
            + "\nCustomer Name: " + customerName + "\nAmount: " + amount);
            Utilities.writeToCentralLog("Inside CreateInvoiceBean.submitInvoice(). \n"
            + "Creating new invoice with number: " + invoiceNumber);
            
            validateInput();

            /*InvoiceDTO newInvoice = new InvoiceDTO();
            newInvoice.setInvoiceNumber(invoiceNumber);
            newInvoice.setInvoiceDate(invoiceDate);
            newInvoice.setInvoiceCustomerName(customerName);
            newInvoice.setInvoiceAmount(amount); */

            // Create invoice using InvoicePdfResource API
            var newInvoice = new InvoiceDTO(null, amount, customerName, invoiceDate, invoiceNumber); 
            Response response = invoicePdfResource.createInvoice(newInvoice);
            
            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                Utilities.writeToCentralLog("Invoice created successfully via API. Invoice number: " + invoiceNumber);
                logger.info("Invoice created successfully via API");
                facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Invoice created successfully"));
                return "listing?faces-redirect=true";
            } else {
                String errorMsg = "Failed to create invoice - API returned status: " + response.getStatus();
                logger.severe(errorMsg);
                Utilities.writeToCentralLog("Error: " + errorMsg);
                facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", errorMsg));
                return null;
            }
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to create invoice", e);
            Utilities.writeToCentralLog("Failed to create invoice. Exception: " + e.getMessage());
            facesContext.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to create invoice: " + e.getMessage()));
            return null;
        }
    }

    private void validateInput() throws Exception {
        if (invoiceNumber == null || invoiceNumber.isBlank()) {
            throw new Exception("Invoice number is required.");
        }
        if (invoiceDate == null) {
            throw new Exception("Invoice date is required.");
        }
        if (customerName == null || customerName.isBlank()) {
            throw new Exception("Customer name is required.");
        }
        if (amount == null || amount.isBlank() || 
            new BigDecimal(amount).compareTo(BigDecimal.ZERO) <= 0) {
            throw new Exception("Amount must be greater than zero.");
        }
    }

    private void navigateToLogin() {
        try {
            externalContext.redirect("login.xhtml");
        } catch (Exception e) {
            logger.severe("Failed to redirect to login: " + e.getMessage());
        }
    }

    // Getters and Setters
    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public LocalDate getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(LocalDate invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public void writeToLog(String message) {
        try {
            Files.write(Paths.get("log.txt"), (message + "\n").getBytes(),
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND); 
        } catch (IOException e) {
            logger.severe("Failed to write to log file: " + e.getMessage());
        }
    }
}
