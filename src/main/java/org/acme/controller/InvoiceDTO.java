package org.acme.controller;

public class InvoiceDTO {

    private Long id;
    private String invoiceNumber;
    private String invoiceDate;
    private String invoiceCustomerName;
    private String invoiceAmount;

    public InvoiceDTO(Long id, String amount, String customerName, String invoiceDate, String invoiceNumber) {
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

    public String getInvoiceDate() {
        return invoiceDate;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    @Override
    public String toString() {
        return "InvoiceDTO{" +
                "id=" + id +
                ", invoiceNumber='" + invoiceNumber + '\'' +
                ", invoiceDate='" + invoiceDate + '\'' +
                ", invoiceCustomerName='" + invoiceCustomerName + '\'' +
                ", invoiceAmount='" + invoiceAmount + '\'' +
                '}';
    }
}

