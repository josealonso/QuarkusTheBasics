import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import org.primefaces.PrimeFaces;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Named
@ViewScoped
public class InvoiceBean implements Serializable {

    @NotEmpty
    @Pattern(regexp = "[0-9]+")
    private Integer invoiceNumber;

    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}")
    private LocalDate invoiceDate;

    @Pattern(regexp = "[a-zA-Z\\s]+")
    private String customerName;

    @Pattern(regexp = "[0-9]+\\.[0-9]{2}")
    private BigDecimal amount;

    public Integer getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(Integer invoiceNumber) {
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

}




























