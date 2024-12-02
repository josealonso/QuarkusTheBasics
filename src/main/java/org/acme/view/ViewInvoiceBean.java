package org.acme.view;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.acme.Utilities;
import org.acme.controller.InvoiceDTO;
import org.acme.exceptions.InvoiceNotFoundException;
import org.acme.model.User;
import org.acme.service.InvoicePdfService;
import org.acme.service.InvoiceService;
import java.util.logging.Logger;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

@Named
@ViewScoped
public class ViewInvoiceBean implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(ViewInvoiceBean.class.getName());
    
    private Long invoiceId;
    private InvoiceDTO invoice;
    private boolean streamMode;
    private User sessionUser;
    
    @Inject
    private InvoiceService invoiceService;
    
    @Inject
    private FacesContext facesContext;
    
    @Inject
    private ExternalContext externalContext;
    
    @Inject
    private InvoicePdfService pdfService;

    private byte[] currentPdfContent;
    private boolean previewMode = false;

    @PostConstruct
    public void init() {
        try {
            // Get session user
            Utilities.writeToCentralLog("Inside ViewInvoiceBean.init()");
            
            Map<String, Object> sessionMap = externalContext.getSessionMap();
            Utilities.writeToCentralLog("Session map contains keys: " + String.join(", ", sessionMap.keySet()));
            
            sessionUser = (User) externalContext.getSessionMap().get("user");
            Utilities.writeToCentralLog("Session user: " + (sessionUser != null ? sessionUser.getEmail() : "null"));
            
            if (sessionUser == null) {
                logger.severe("No user in session");
                facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No user in session"));
                Utilities.writeToCentralLog("No user in session");
                navigateToLogin();
                return;
            }
            
            logger.info("Init called for user: " + sessionUser.getEmail());
            
            String idParam = externalContext.getRequestParameterMap().get("id");
            String streamParam = externalContext.getRequestParameterMap().get("stream");
            streamMode = "true".equals(streamParam);
            
            Utilities.writeToCentralLog("Processing parameters - ID: " + idParam + ", stream: " + streamMode);
            
            if (idParam != null && !idParam.trim().isEmpty()) {
                try {
                    invoiceId = Long.parseLong(idParam);
                    Utilities.writeToCentralLog("Looking up invoice with ID: " + invoiceId);
                    
                    invoice = invoiceService.getInvoiceById(invoiceId)
                            .orElseThrow(() -> new InvoiceNotFoundException("Invoice not found with ID: " + invoiceId));
                    
                    Utilities.writeToCentralLog("Invoice found: " + invoice.getInvoiceNumber() + " for customer: " + invoice.getInvoiceCustomerName());
                    
                    // Check if user has access to this invoice
                    /*if (!invoice.getInvoiceCustomerName().equals(sessionUser.getEmail())) {
                        logger.severe("User " + sessionUser.getEmail() + " attempted to access invoice: " + invoice.getInvoiceNumber());
                        facesContext.addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "You don't have permission to view this invoice"));
                        Utilities.writeToCentralLog("Access denied - Invoice customer: " + invoice.getInvoiceCustomerName() + " != Session user: " + sessionUser.getEmail());
                        navigateToListing();
                        return;
                    } */
                    
                    Utilities.writeToCentralLog("Access granted - Loading invoice view");
                    
                    if (streamMode) {
                        Utilities.writeToCentralLog("Streaming PDF for invoice: " + invoice.getInvoiceNumber());
                        streamPdf();
                    }
                } catch (NumberFormatException e) {
                    logger.severe("Invalid invoice ID format: " + idParam);
                    facesContext.addMessage(null, 
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Invalid invoice ID format"));
                    navigateToListing();
                } catch (InvoiceNotFoundException e) {
                    logger.severe("Invoice not found: " + e.getMessage());
                    facesContext.addMessage(null, 
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
                    navigateToListing();
                }
            } else {
                logger.warning("No ID parameter provided");
            }
        } catch (Exception e) {
            logger.severe("Error in init(): " + e.getMessage());
            facesContext.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to initialize: " + e.getMessage()));
        }
    }

    public String previewPdf() {
        logger.info("Preview PDF called. Invoice: " + (invoice != null ? invoice.getInvoiceNumber() : "null"));
        Utilities.writeToCentralLog("Preview PDF called for invoice: " + (invoice != null ? invoice.getInvoiceNumber() : "null"));
        
        if (invoice == null) {
            facesContext.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No invoice selected"));
            Utilities.writeToCentralLog("No invoice selected for preview");
            return null;
        }

        try {
            String viewUrl = "viewPdf.xhtml?id=" + invoice.getId() + "&faces-redirect=true";
            return viewUrl;
        } catch (Exception e) {
            String errorMsg = "Failed to generate PDF preview: " + e.getMessage();
            logger.severe(errorMsg);
            Utilities.writeToCentralLog(errorMsg);
            facesContext.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", errorMsg));
            return null;
        }
    }

    public void downloadPdf() {
        logger.info("Download PDF called. Invoice: " + (invoice != null ? invoice.getInvoiceNumber() : "null"));
        
        if (invoice == null) {
            facesContext.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No invoice selected"));
            return;
        }

        try {
            byte[] pdfBytes = currentPdfContent != null ? currentPdfContent : pdfService.generateInvoicePdf(invoice, sessionUser);
            
            externalContext.responseReset();
            externalContext.setResponseContentType("application/pdf");
            externalContext.setResponseHeader("Content-Disposition", 
                "attachment; filename=\"invoice-" + invoice.getInvoiceNumber() + ".pdf\"");
            externalContext.setResponseContentLength(pdfBytes.length);
            
            externalContext.getResponseOutputStream().write(pdfBytes);
            facesContext.responseComplete();
            
            logger.info("PDF downloaded successfully");
        } catch (Exception e) {
            logger.severe("Failed to download PDF: " + e.getMessage());
            facesContext.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to generate PDF: " + e.getMessage()));
        }
    }

    public void streamPdf() {
        logger.info("Stream PDF called. Invoice: " + (invoice != null ? invoice.getInvoiceNumber() : "null"));
        
        if (invoice == null) {
            String idParam = externalContext.getRequestParameterMap().get("id");
            if (idParam != null && !idParam.trim().isEmpty()) {
                try {
                    Long id = Long.parseLong(idParam);
                    invoice = invoiceService.getInvoiceById(id)
                            .orElseThrow(() -> new InvoiceNotFoundException("Invoice not found with ID: " + id));
                } catch (NumberFormatException e) {
                    logger.severe("Invalid invoice ID format: " + idParam);
                    return;
                } catch (InvoiceNotFoundException e) {
                    logger.severe("Invoice not found: " + e.getMessage());
                    return;
                }
            } else {
                logger.severe("No invoice ID provided");
                return;
            }
        }

        try {
            if (currentPdfContent == null) {
                currentPdfContent = pdfService.generateInvoicePdf(invoice, sessionUser);
            }
            
            externalContext.responseReset();
            externalContext.setResponseContentType("application/pdf");
            externalContext.setResponseHeader("Content-Disposition", "inline; filename=\"invoice-" + invoice.getInvoiceNumber() + ".pdf\"");
            externalContext.setResponseHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            externalContext.setResponseHeader("Pragma", "no-cache");
            externalContext.setResponseHeader("Expires", "0");
            externalContext.setResponseContentLength(currentPdfContent.length);
            
            externalContext.getResponseOutputStream().write(currentPdfContent);
            facesContext.responseComplete();
            
            logger.info("PDF streamed successfully");
        } catch (Exception e) {
            logger.severe("Failed to stream PDF: " + e.getMessage());
        }
    }

    public void closePreview() {
        previewMode = false;
        currentPdfContent = null;
        logger.info("Preview closed");
    }

    private void navigateToListing() {
        try {
            externalContext.redirect("listing.xhtml");
        } catch (IOException e) {
            logger.severe("Navigation failed: " + e.getMessage());
            facesContext.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Navigation failed"));
        }
    }

    private void navigateToLogin() {
        try {
            externalContext.redirect("login.xhtml");
        } catch (IOException e) {
            logger.severe("Navigation failed: " + e.getMessage());
            facesContext.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Navigation failed"));
        }
    }

    // Getters and Setters
    public Long getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(Long invoiceId) {
        this.invoiceId = invoiceId;
    }

    public InvoiceDTO getInvoice() {
        return invoice;
    }

    public void setInvoice(InvoiceDTO invoice) {
        this.invoice = invoice;
    }

    public boolean isPreviewMode() {
        return previewMode;
    }

    public void setPreviewMode(boolean previewMode) {
        this.previewMode = previewMode;
    }

    public boolean isStreamMode() {
        return streamMode;
    }

    public void setStreamMode(boolean streamMode) {
        this.streamMode = streamMode;
    }
}
