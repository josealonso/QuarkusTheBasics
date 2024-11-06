import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.acme.controller.Invoice;
import org.acme.service.InvoiceService;

import java.io.Serializable;
import java.util.List;

@Named("userInvoiceOptionsBean")
@ViewScoped
public class UserInvoiceOptionsBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Invoice> userInvoices;

    @Inject
    private InvoiceService invoiceService;

    @Inject
    private FacesContext facesContext;

    public void setUserInvoices(List<Invoice> userInvoices) {
        this.userInvoices = userInvoices;
    }

    public List<Invoice> getUserInvoices() {
            userInvoices = invoiceService.getUserInvoices();
            return userInvoices;
//        return List.of(
//            new Invoice(0, "1", "2023-06-01", "John Doe", "100.00"),
//            new Invoice(1, "2", "2023-06-02", "Alice Doe", "200.00")
//        );
    }

    public String viewInvoice(Long id) {
        // Logic to view an invoice
        // This could navigate to a new page or open a dialog
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Viewing invoice", "Invoice ID: " + id));
        return "viewInvoice.xhtml?faces-redirect=true&id=" + id;
    }

    public void deleteInvoice(Long id) {
        try {
            invoiceService.deleteInvoice(id);
            userInvoices.removeIf(invoice -> {
                return invoice.getId() == id;
            });
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Invoice deleted successfully"));
        } catch (Exception e) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to delete invoice"));
        }
    }

    public String prepareNewInvoice() {
        // Navigate to a page where there is a form to create the invoice
        return "newInvoice.xhtml?faces-redirect=true";
    }
}
