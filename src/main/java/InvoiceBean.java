import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import org.primefaces.PrimeFaces;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Named
@ViewScoped
public class InvoiceBean implements Serializable {
    private String invoiceNumber;
    private LocalDate invoiceDate;
    private String customerName;
    private BigDecimal amount;

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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void submitInvoice() {
        if (allTheFieldsAreFilledAndValid()) {
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

    private boolean allTheFieldsAreFilledAndValid() {
        boolean validNumber = false;
        boolean validDate = true;  // TODO   // date validation is not working
        boolean validName = false;
        boolean validAmount = false;

        if (getInvoiceNumber().matches("[0-9].00*")) {
            validNumber = true;
        }
        try {
            LocalDate.parse(getInvoiceDate().toString(), DateTimeFormatter.ISO_DATE);
            validDate = true;
        } catch (Exception ignored) {

        }

        if (getCustomerName().matches("[a-zA-Z\\s]+")) {
            validName = true;
        }
        if (getAmount().toString().matches("[0-9]+\\.[0-9]{2}")) {
            validAmount = true;
        }

        if (validNumber && validDate && validName && validAmount) {
            return true;
        }
        else {
            var errorMessage = buildErrorMessage(validNumber, validDate, validName, validAmount);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_ERROR, errorMessage, null));
            return false;
        }
    }

    private String buildErrorMessage(boolean validNumber, boolean validDate, boolean validName, boolean validAmount) {
        StringBuilder sb = new StringBuilder();
        if (!validAmount) {
            sb.append("Error - Invalid amount\n");
        }
        if (!validName) {
            sb.append("Error - Invalid customer name\n");
        }
        if (!validDate) {
            sb.append("Error - Invalid invoice date\n");
        }
        if (!validNumber) {
            sb.append("Error - Invalid invoice number\n");
        }
        return sb.toString();
    }
}




























