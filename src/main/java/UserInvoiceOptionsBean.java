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
//            userInvoices = invoiceService.getUserInvoices();
//            return userInvoices;
        return List.of(
                Invoice.builder().invoiceNumber("1").invoiceDate("2023-06-01").customerName("John Doe").amount("100.00").build(),
                Invoice.builder().invoiceNumber("2").invoiceDate("2023-06-02").customerName("Alice Doe").amount("200.00").build()
        );
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
                assert invoice.getId() != null;
                return invoice.getId().equals(id);
            });
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Invoice deleted successfully"));
        } catch (Exception e) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to delete invoice"));
        }
    }

    public String prepareNewInvoice() {
        // Logic to prepare for creating a new invoice
        // This could navigate to a new page or open a dialog
        return "createInvoice.xhtml?faces-redirect=true";
    }
}
