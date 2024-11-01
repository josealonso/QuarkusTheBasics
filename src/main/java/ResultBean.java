import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.Date;

@Named
@ViewScoped
public class ResultBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private String endpointResponse;
    private Date submissionTime;

    private String invoiceNumber;
    private Date invoiceDate;
    private String customerName;
    private String amount;

    @PostConstruct
    public void init() {
        submissionTime = new Date();
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO,
                        "Message Summary: " + getInvoiceNumber(), ""));

        callEndpoint();
    }

    private void callEndpoint() {
        try {
            // Simulate an endpoint call
            Thread.sleep(1000);
            endpointResponse = "Endpoint called successfully at " + new Date();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            endpointResponse = "Error calling endpoint: " + e.getMessage();
        }
    }

    // Getters and setters

    public String getEndpointResponse() {
        return endpointResponse;
    }

    public void setEndpointResponse(String endpointResponse) {
        this.endpointResponse = endpointResponse;
    }

    public Date getSubmissionTime() {
        return submissionTime;
    }

    public void setSubmissionTime(Date submissionTime) {
        this.submissionTime = submissionTime;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Date getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(Date invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

}
