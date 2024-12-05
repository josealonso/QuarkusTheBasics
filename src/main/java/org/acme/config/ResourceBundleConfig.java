package org.acme.config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import jakarta.faces.context.FacesContext;
import java.util.Locale;
import java.util.ResourceBundle;
import org.acme.Utilities;

@Named
@ApplicationScoped
public class ResourceBundleConfig {
    private ResourceBundle resourceBundle;
    private Locale currentLocale;

    public ResourceBundleConfig() {
        this.currentLocale = Locale.ENGLISH;
        initResourceBundle();
    }

    private void initResourceBundle() {
        try {
            Utilities.writeToCentralLog("Loading ResourceBundle for locale: " + currentLocale);
            this.resourceBundle = ResourceBundle.getBundle("messages", currentLocale);
            Utilities.writeToCentralLog("ResourceBundle loaded successfully.");
        } catch (Exception e) {
            Utilities.writeToCentralLog("ResourceBundle not found: " + e.getMessage());
            try {
                // Fallback to default ResourceBundle
                this.resourceBundle = ResourceBundle.getBundle("messages");
                Utilities.writeToCentralLog("Loaded default ResourceBundle.");
            } catch (Exception ex) {
                Utilities.writeToCentralLog("Failed to load default ResourceBundle: " + ex.getMessage());
            }
        }
    }

    public void setLocale(Locale locale) {
        this.currentLocale = locale;
        initResourceBundle();
    }

    public ResourceBundle getResourceBundle() {
        if (resourceBundle == null) {
            initResourceBundle();
        }
        return resourceBundle;
    }

    public Locale getCurrentLocale() {
        return currentLocale;
    }
}
