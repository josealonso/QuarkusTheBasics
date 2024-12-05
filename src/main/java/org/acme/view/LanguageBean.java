package org.acme.view;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.acme.config.ResourceBundleConfig;
import org.acme.Utilities;

import java.io.IOException;
import java.io.Serializable;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.HashMap;
import java.util.Map;

@Named
@SessionScoped
public class LanguageBean implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String localeCode = "en";
    
    private static Map<String, Object> countries;
    static {
        countries = new HashMap<>();
        countries.put("English", "en");
        countries.put("Español", "es");
        countries.put("Deutsch", "de");
    }

    public Map<String, Object> getCountries() {
        return countries;
    }

    public String getLocaleCode() {
        return localeCode;
    }

    public void setLocaleCode(String localeCode) {
        this.localeCode = localeCode;
    }

    public void changeLanguage() {
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            Locale locale = new Locale(localeCode);
            context.getViewRoot().setLocale(locale);
            
            // Update ResourceBundle
            ResourceBundle.clearCache();
            ResourceBundle.getBundle("messages", locale);
            
            // Update ResourceBundleConfig
            updateResourceBundleConfig(locale);
            
            // Redirect to the same page to refresh with new locale
            String viewId = context.getViewRoot().getViewId();
            ExternalContext externalContext = context.getExternalContext();
            externalContext.redirect(externalContext.getRequestContextPath() + viewId);
        } catch (Exception e) {
            Utilities.writeToCentralLog("Error changing language: " + e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error changing language", e.getMessage()));
        }
    }

    private void updateResourceBundleConfig(Locale locale) {
        try {
            // Attempt to update ResourceBundleConfig if it exists
            FacesContext context = FacesContext.getCurrentInstance();
            ResourceBundleConfig bundleConfig = context.getApplication()
                .evaluateExpressionGet(context, "#{resourceBundleConfig}", ResourceBundleConfig.class);
            
            if (bundleConfig != null) {
                bundleConfig.setLocale(locale);
            }
        } catch (Exception e) {
            // Silently handle if no such bean exists
            e.printStackTrace();
        }
    }
}
