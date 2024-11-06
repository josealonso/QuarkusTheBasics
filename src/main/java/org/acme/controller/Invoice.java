package org.acme.controller;

public class Invoice {

    private int id;
    private String invoiceNumber;
    private String invoiceDate;
    private String customerName;
    private String amount;

    public Invoice(int id, String amount, String customerName, String invoiceDate, String invoiceNumber) {
        this.id = id;
        this.amount = amount;
        this.customerName = customerName;
        this.invoiceDate = invoiceDate;
        this.invoiceNumber = invoiceNumber;
    }

    public int getId() {
        return id;
    }

    public String getAmount() {
        return amount;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getInvoiceDate() {
        return invoiceDate;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }
}

