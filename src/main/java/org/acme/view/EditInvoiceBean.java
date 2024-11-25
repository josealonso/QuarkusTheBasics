package org.acme.view;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;

import org.acme.Utilities;
import org.acme.controller.InvoiceDTO;
import org.acme.exceptions.InvoiceNotFoundException;
import org.acme.model.User;
import org.acme.resource.InvoicePdfResource;

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
    private InvoicePdfResource invoicePdfResource;

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
                    Response response = invoicePdfResource.getInvoice(invoiceId);
                    
                    if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                        invoiceToSave = response.readEntity(InvoiceDTO.class);
                        logger.info("Successfully loaded invoice: " + invoiceToSave);
                    } else {
                        String errorMsg = "Failed to load invoice - API returned status: " + response.getStatus();
                        logger.severe(errorMsg);
                        throw new InvoiceNotFoundException(errorMsg);
                    }
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

            // Update invoice using InvoicePdfResource API
            Response response = invoicePdfResource.updateInvoice(invoiceToSave.getId(), invoiceToSave);
            
            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                InvoiceDTO updatedInvoice = response.readEntity(InvoiceDTO.class);
                invoiceToSave = updatedInvoice;
                
                Utilities.writeToCentralLog("Invoice updated successfully via API. InvoiceDTO: " + invoiceToSave);
                logger.info("Invoice updated successfully via API");
                facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Invoice updated successfully"));
                return "listing?faces-redirect=true";
            } else {
                String errorMsg = "Failed to update invoice - API returned status: " + response.getStatus();
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
