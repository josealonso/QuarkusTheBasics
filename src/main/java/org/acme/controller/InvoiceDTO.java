package org.acme.controller;

import java.time.LocalDate;

public class InvoiceDTO {

    private Long id;
    private String invoiceNumber;
    private LocalDate invoiceDate;
    private String invoiceCustomerName;
    private String invoiceAmount;

    public InvoiceDTO(Long id, String amount, String customerName, LocalDate invoiceDate, String invoiceNumber) {
        this.id = id;
        this.invoiceAmount = amount;
        this.invoiceCustomerName = customerName;
        this.invoiceDate = invoiceDate;
        this.invoiceNumber = invoiceNumber;
    }

    public Long getId() {
        return id;
    }

    public String getInvoiceAmount() {
        return invoiceAmount;
    }

    public String getInvoiceCustomerName() {
        return invoiceCustomerName;
    }

    public LocalDate getInvoiceDate() {
        return invoiceDate;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public void setInvoiceDate(LocalDate invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public void setInvoiceCustomerName(String invoiceCustomerName) {
        this.invoiceCustomerName = invoiceCustomerName;
    }

    public void setInvoiceAmount(String invoiceAmount) {
        this.invoiceAmount = invoiceAmount;
    }

    @Override
    public String toString() {
        return "InvoiceDTO{" +
                "id=" + id +
                ", invoiceNumber='" + invoiceNumber + '\'' +
                ", invoiceDate=" + invoiceDate +
                ", invoiceCustomerName='" + invoiceCustomerName + '\'' +
                ", invoiceAmount='" + invoiceAmount + '\'' +
                '}';
    }
}
