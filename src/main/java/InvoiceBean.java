import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.primefaces.PrimeFaces;

import java.io.Serializable;
import java.time.LocalDate;

@Named
@ViewScoped
public class InvoiceBean implements Serializable {

    @NotEmpty(message = "Invoice Number cannot not be empty")
    @Pattern(regexp = "^\\d+$", message = "Invoice number must contain only digits")
    private String invoiceNumber;

    @NotNull(message = "Invoice Date cannot be empty")
    // @Pattern(regexp = "^[0-9]{4}-[0-9]{2}-[0-9]{2}$", message = "Date must be in format yyyy-MM-dd")
    // IMPORTANT: @Pattern is not valid for LocalDate
    private LocalDate invoiceDate;

    @NotEmpty(message = "Customer name cannot be empty")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Customer name must contain only letters and spaces")
    private String customerName;

    @NotEmpty(message = "Invoice Amount cannot be empty")
    @Pattern(regexp = "^[0-9]+(\\.[0-9]{1,2})?$", message = "Amount must be a number with up to two decimal places")
    private String amount;

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

    public void submitInvoice() {
        if (1 == 1) { // allTheFieldsAreFilledAndValid()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Success", "- Invoice submitted successfully"));
            // Update messages component
            PrimeFaces.current().ajax().update("messages");
            PrimeFaces.current()
                    .executeScript("setTimeout(function() { window.location.href = 'result.xhtml';}, 1000)");
        }
    }

    public Integer getInvoiceNumberAsInteger() {
        return Integer.parseInt(getInvoiceNumber());
    }
}




























