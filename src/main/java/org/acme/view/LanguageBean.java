package org.acme.view;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
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
    
    @Inject
    private ResourceBundleConfig resourceBundleConfig;
    
    @Inject
    private FacesContext facesContext;
    
    private String localeCode = "en";
    
    private static Map<String, Object> countries;
    static {
        countries = new HashMap<>();
        countries.put("English", "en");
        countries.put("Español", "es");
        countries.put("Deutsch", "de");
    }

    @PostConstruct
    public void init() {
        // Try to get locale from session
        if (facesContext != null) {
            Locale sessionLocale = (Locale) facesContext.getExternalContext().getSessionMap().get("locale");
            if (sessionLocale != null) {
                this.localeCode = sessionLocale.getLanguage();
                resourceBundleConfig.setLocale(sessionLocale);
                facesContext.getViewRoot().setLocale(sessionLocale);
            }
        }
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
            // Ensure the localeCode is not null and valid
            if (localeCode == null || localeCode.isEmpty()) {
                localeCode = "en"; // default to English
            }

            // Create Locale object
            Locale locale = new Locale(localeCode);
            
            // Update session and view root locale
            FacesContext facesContext = FacesContext.getCurrentInstance();
            ExternalContext externalContext = facesContext.getExternalContext();
            
            // Set locale in session
            externalContext.getSessionMap().put("locale", locale);
            
            // Update ResourceBundleConfig
            resourceBundleConfig.setLocale(locale);
            
            // Update FacesContext view root locale
            facesContext.getViewRoot().setLocale(locale);
            
            // Prepare redirect
            String viewId = facesContext.getViewRoot().getViewId();
            String redirectUrl = externalContext.getRequestContextPath() + viewId + 
                                 "?faces-redirect=true&includeViewParams=true";
            
            // Redirect to preserve language
            externalContext.redirect(redirectUrl);
            
            // Log the language change
            Utilities.writeToCentralLog("Language changed to: " + localeCode);
        } catch (IOException e) {
            Utilities.writeToCentralLog("Error changing language: " + e.getMessage());
        }
    }

    private void updateResourceBundleConfig(Locale locale) {
        try {
            // Attempt to update ResourceBundleConfig if it exists
            ResourceBundleConfig bundleConfig = facesContext.getApplication()
                .evaluateExpressionGet(facesContext, "#{resourceBundleConfig}", ResourceBundleConfig.class);
            
            if (bundleConfig != null) {
                bundleConfig.setLocale(locale);
            }
        } catch (Exception e) {
            // Silently handle if no such bean exists
            e.printStackTrace();
        }
    }
}
