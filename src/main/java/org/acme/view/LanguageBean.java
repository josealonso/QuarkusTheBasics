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
            Locale locale = new Locale(localeCode);
            
            // Update ResourceBundleConfig first (it will handle session and ViewRoot)
            resourceBundleConfig.setLocale(locale);
            
            // Clear resource bundle caches
            ResourceBundle.clearCache();
            ResourceBundle.clearCache(Thread.currentThread().getContextClassLoader());
            
            // Force a redirect to refresh the page while preserving view parameters
            String viewId = facesContext.getViewRoot().getViewId();
            ExternalContext externalContext = facesContext.getExternalContext();
            
            // Get the current query string using HttpServletRequest
            HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();
            String currentQueryString = request.getQueryString();
            
            StringBuilder redirectUrl = new StringBuilder(externalContext.getRequestContextPath())
                .append(viewId)
                .append("?faces-redirect=true&includeViewParams=true");
                
            // Preserve existing query parameters if they exist
            if (currentQueryString != null && !currentQueryString.isEmpty() && 
                !currentQueryString.contains("faces-redirect=true")) {
                redirectUrl.append("&").append(currentQueryString);
            }
            
            // Preserve theme and styling context
            externalContext.getSessionMap().put("preserveThemeContext", true);
            
            externalContext.redirect(redirectUrl.toString());
            
            Utilities.writeToCentralLog("Language changed to: " + localeCode + " with URL: " + redirectUrl);
        } catch (Exception e) {
            Utilities.writeToCentralLog("Error changing language: " + e.getMessage());
            facesContext.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error changing language", e.getMessage()));
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
