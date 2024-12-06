package org.acme.config;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.ResourceBundle;
import java.util.Locale;
import org.acme.Utilities;

@Named("msg")
@RequestScoped  // Changed to RequestScoped to ensure we get fresh messages each request
public class MessageProvider {
    
    @Inject
    private FacesContext facesContext;
    
    private ResourceBundle bundle;

    @PostConstruct
    public void init() {
        updateBundle();
        Utilities.writeToCentralLog("ResourceBundle initialized in MessageProvider");
    }

    public ResourceBundle getValue() {
        updateBundle();
        return bundle;
    }

    public String getString(String key) {
        updateBundle();
        try {
            return bundle.getString(key);
        } catch (Exception e) {
            Utilities.writeToCentralLog("Error getting string for key: " + key + " - " + e.getMessage());
            return "???" + key + "???";
        }
    }

    public Object get(String key) {
        return getString(key);
    }

    // This method allows property-style access in EL expressions
    public String getAsProperty(String key) {
        return getString(key);
    }

    private void updateBundle() {
        try {
            if (facesContext != null && facesContext.getViewRoot() != null) {
                // First try to get locale from session
                Locale locale = (Locale) facesContext.getExternalContext().getSessionMap().get("locale");
                if (locale == null) {
                    // Fallback to ViewRoot locale
                    locale = facesContext.getViewRoot().getLocale();
                }
                
                // Use the application's resource bundle with explicit locale
                bundle = ResourceBundle.getBundle("messages", locale);
                Utilities.writeToCentralLog("ResourceBundle updated with locale: " + locale);
            } else {
                bundle = ResourceBundle.getBundle("messages");
                Utilities.writeToCentralLog("Using default ResourceBundle");
            }
        } catch (Exception e) {
            Utilities.writeToCentralLog("Error updating ResourceBundle: " + e.getMessage());
            e.printStackTrace();
            bundle = ResourceBundle.getBundle("messages");
        }
    }
}
