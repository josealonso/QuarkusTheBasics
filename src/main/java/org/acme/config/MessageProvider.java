package org.acme.config;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import java.util.ResourceBundle;
import java.util.Locale;
import org.acme.Utilities;

@Named("msg")
@ApplicationScoped
public class MessageProvider {

    private ResourceBundle bundle;

    @PostConstruct
    public void init() {
        updateBundle();
        Utilities.writeToCentralLog("ResourceBundle initialized in MessageProvider");
    }

    // This method will be called when using #{msg.value['key']}
    public ResourceBundle getValue() {
        updateBundle(); // Update bundle with current locale
        Utilities.writeToCentralLog("getValue() called in MessageProvider");
        return bundle;
    }

    // This method will be called when using #{msg.string['key']}
    public String getString(String key) {
        updateBundle(); // Update bundle with current locale
        Utilities.writeToCentralLog("getString() called in MessageProvider with key: " + key);
        return bundle.getString(key);
    }

    private void updateBundle() {
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            if (context != null) {
                Locale locale = context.getViewRoot().getLocale();
                bundle = ResourceBundle.getBundle("messages", locale);
                Utilities.writeToCentralLog("ResourceBundle updated with locale: " + locale);
            } else {
                bundle = ResourceBundle.getBundle("messages");
                Utilities.writeToCentralLog("ResourceBundle updated with default locale");
            }
        } catch (Exception e) {
            Utilities.writeToCentralLog("Error updating ResourceBundle: " + e.getMessage());
            bundle = ResourceBundle.getBundle("messages");
        }
    }
}
