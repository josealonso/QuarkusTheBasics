import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.acme.controller.WordDocumentResourceController;
import org.acme.service.WordDocumentGenerationService;
import org.primefaces.PrimeFaces;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Named
@ViewScoped
public class InvoiceBeanController implements Serializable {

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

        FacesContext context = FacesContext.getCurrentInstance();
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        try {
            if (invoiceNumber.isBlank()) {
                throw new Exception("Invoice number is required.");
            }
            if (invoiceDate == null) {
                throw new Exception("Invoice date is required.");
            }
            if (customerName.isBlank()) {
                throw new Exception("Customer name is required.");
            }
            if (amount.isBlank() ||
                    new BigDecimal(amount).compareTo(BigDecimal.ZERO) <= 0) {
                throw new Exception("Amount is required.");
            }
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
//            throw new RuntimeException(e);
        }

        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Success", "- Invoice submitted successfully with these data: "
        + "\nInvoice Number: " + invoiceNumber
        + "\nInvoice Date: " + invoiceDate
        + "\nCustomer Name: " + customerName
        + "\nAmount: " + amount));
        PrimeFaces.current().ajax().update("messages");
        clearForm();
        callEndpoint(invoiceNumber, invoiceDate, customerName, amount);
//            PrimeFaces.current()
//                    .executeScript("setTimeout(function() { window.location.href = 'result.xhtml';}, 1000)");
    }

    private void callEndpoint(String invoiceNumber, LocalDate invoiceDate, String customerName, String amount) {
        try {
            new WordDocumentResourceController(new WordDocumentGenerationService())
                    .submitForm(invoiceNumber, String.valueOf(invoiceDate), customerName, amount);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Integer getInvoiceNumberAsInteger() {
        return Integer.parseInt(getInvoiceNumber());
    }

    private void clearForm() {
        invoiceNumber = null;
        invoiceDate = null;
        customerName = null;
        amount = null;
    }

}




























