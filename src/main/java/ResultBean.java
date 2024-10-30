import jakarta.annotation.PostConstruct;
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

    @PostConstruct
    public void init() {
        submissionTime = new Date();
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
}
