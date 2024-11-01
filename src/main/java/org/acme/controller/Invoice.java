package org.acme.controller;

public class Invoice {
    private final String invoiceNumber;
    private final String invoiceDate;
    private final String customerName;
    private final String amount;

    private Invoice(Builder builder) {
        this.invoiceNumber = builder.invoiceNumber;
        this.invoiceDate = builder.invoiceDate;
        this.customerName = builder.customerName;
        this.amount = builder.amount;
    }

    // Getters
    public String getInvoiceNumber() { return invoiceNumber; }
    public String getInvoiceDate() { return invoiceDate; }
    public String getCustomerName() { return customerName; }
    public String getAmount() { return amount; }

    public static class Builder {
        private String invoiceNumber;
        private String invoiceDate;
        private String customerName;
        private String amount;

        public Builder invoiceNumber(String invoiceNumber) {
            this.invoiceNumber = invoiceNumber;
            return this;
        }

        public Builder invoiceDate(String invoiceDate) {
            this.invoiceDate = invoiceDate;
            return this;
        }

        public Builder customerName(String customerName) {
            this.customerName = customerName;
            return this;
        }

        public Builder amount(String amount) {
            this.amount = amount;
            return this;
        }

        public Invoice build() {
            return new Invoice(this);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
