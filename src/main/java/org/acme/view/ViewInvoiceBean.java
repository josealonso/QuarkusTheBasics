package org.acme.view;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.acme.controller.InvoiceDTO;
import org.acme.exceptions.InvoiceNotFoundException;
import org.acme.service.InvoiceService;

import java.io.IOException;
import java.io.Serializable;

@Named
@RequestScoped
public class ViewInvoiceBean implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Long invoiceId;
    private InvoiceDTO invoice;
    
    @Inject
    private InvoiceService invoiceService;
    
    @Inject
    private FacesContext facesContext;
    
    @Inject
    private ExternalContext externalContext;

    @PostConstruct
    public void init() {
        if (invoiceId != null) {
            try {
                invoice = invoiceService.getInvoiceById(invoiceId)
                        .orElseThrow(() -> new InvoiceNotFoundException("Invoice not found"));
            } catch (InvoiceNotFoundException e) {
                facesContext.addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Invoice not found"));
                navigateToListing();
            }
        }
    }

    public void deleteInvoice() {
        try {
            invoiceService.deleteInvoice(invoice.getInvoiceNumber().toString());
            facesContext.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Invoice deleted successfully"));
            navigateToListing();
        } catch (Exception e) {
            facesContext.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to delete invoice"));
        }
    }

    public void downloadPdf() {
        try {
            // TODO: Implement PDF generation and download
            // For now, just show a message
            facesContext.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "PDF download feature coming soon"));
        } catch (Exception e) {
            facesContext.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to download PDF"));
        }
    }

    private void navigateToListing() {
        try {
            externalContext.redirect("listing.xhtml");
        } catch (IOException e) {
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
}
