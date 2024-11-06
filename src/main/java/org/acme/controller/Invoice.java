package org.acme.controller;

public record Invoice(String invoiceNumber, String invoiceDate, String customerName, String amount) {

    public Object getId() {
        return null;
    }

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
            return new Invoice(invoiceNumber, invoiceDate, customerName, amount);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
