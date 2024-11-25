package org.acme.view;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.transaction.Transactional;

import org.acme.Utilities;
import org.acme.controller.InvoiceDTO;
import org.acme.exceptions.InvoiceNotFoundException;
import org.acme.model.User;
import org.acme.service.InvoiceService;

import java.io.Serializable;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named
@ViewScoped
public class EditInvoiceBean implements Serializable {
    private static final Logger logger = Logger.getLogger(EditInvoiceBean.class.getName());
    private static final long serialVersionUID = 1L;

    @Inject
    private FacesContext facesContext;

    @Inject
    private ExternalContext externalContext;

    @Inject
    private InvoiceService invoiceService;

    private InvoiceDTO invoiceToSave;
    private User sessionUser;

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
                return;
            }

            String idParam = externalContext.getRequestParameterMap().get("id");
            if (idParam != null && !idParam.trim().isEmpty()) {
                try {
                    Long invoiceId = Long.parseLong(idParam);
                    invoiceToSave = invoiceService.getInvoiceById(invoiceId)
                            .orElseThrow(() -> new InvoiceNotFoundException("Invoice not found with ID: " + invoiceId));
                } catch (NumberFormatException e) {
                    logger.log(Level.SEVERE, "Invalid invoice ID format: " + idParam, e);
                    facesContext.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Invalid invoice ID format"));
                    navigateToListing();
                } catch (InvoiceNotFoundException e) {
                    logger.log(Level.SEVERE, "Invoice not found", e);
                    facesContext.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
                    navigateToListing();
                }
            } else {
                facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No invoice ID provided"));
                navigateToListing();
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to initialize EditInvoiceBean", e);
            facesContext.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to initialize: " + e.getMessage()));
        }
    }

    @Transactional
    public String save() {
        try {
            logger.info("Saving invoice with ID: " + invoiceToSave.getId());
            Utilities.writeToCentralLog("Saving invoice with ID: " + invoiceToSave.getId() + ". InvoiceDTO: " + invoiceToSave);
            
            if (invoiceToSave == null) {
                String errorMsg = "No invoice to save";
                logger.severe(errorMsg);
                Utilities.writeToCentralLog("Error: " + errorMsg);
                facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", errorMsg));
                return null;
            }

            // Update invoice using service (which is @Transactional)
            invoiceToSave = invoiceService.updateInvoice(invoiceToSave);
            
            if (invoiceToSave != null) {
                Utilities.writeToCentralLog("Invoice updated successfully. InvoiceDTO: " + invoiceToSave);
                logger.info("Invoice updated successfully");
                facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Invoice updated successfully"));
                return "listing?faces-redirect=true";
            } else {
                String errorMsg = "Failed to update invoice - no response from service";
                logger.severe(errorMsg);
                Utilities.writeToCentralLog("Error: " + errorMsg);
                facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", errorMsg));
                return null;
            }
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to save invoice", e);
            Utilities.writeToCentralLog("Failed to save invoice. Exception: " + e.getMessage());
            facesContext.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to update invoice: " + e.getMessage()));
            return null;
        }
    }

    private void navigateToLogin() {
        try {
            externalContext.redirect("login.xhtml");
        } catch (Exception e) {
            logger.severe("Failed to redirect to login: " + e.getMessage());
        }
    }

    private void navigateToListing() {
        try {
            externalContext.redirect("listing.xhtml");
        } catch (Exception e) {
            logger.severe("Failed to redirect to listing: " + e.getMessage());
        }
    }

    public InvoiceDTO getInvoiceToSave() {
        return invoiceToSave;
    }

    public void setInvoiceToSave(InvoiceDTO invoice) {
        this.invoiceToSave = invoice;
    }
}