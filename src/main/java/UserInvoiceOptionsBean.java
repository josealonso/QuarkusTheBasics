import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.acme.controller.InvoiceDTO;
import org.acme.service.InvoiceService;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.util.List;

@Named("userInvoiceOptionsBean")
@ViewScoped
public class UserInvoiceOptionsBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<InvoiceDTO> userInvoices;

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

    public List<InvoiceDTO> getUserInvoices() {
        userInvoices = invoiceService.getAllInvoices();
        writeLogs("FFFF11111FFFFFFF - Got " + userInvoices.size() + " invoices");
        return userInvoices;
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
