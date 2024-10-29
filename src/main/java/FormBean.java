import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;

@Named
@RequestScoped
public class FormBean {
    private String selectedForm = "invoice"; // Default selection

    public String getSelectedForm() {
        return selectedForm;
    }

    public void setSelectedForm(String selectedForm) {
        this.selectedForm = selectedForm;
    }
}
