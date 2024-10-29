package org.acme.jasperreports;

public class JasperInvoiceBean {
    private String name;
    private String country;

    // constructor
    public JasperInvoiceBean(String name, String country) {
        this.name = name;
        this.country = country;
    }

    // getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public String toString() {
        return "InvoiceBean [name=" + name + ", country=" + country + "]";
    }
}
