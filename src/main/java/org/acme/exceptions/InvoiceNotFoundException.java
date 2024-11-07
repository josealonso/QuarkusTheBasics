package org.acme.exceptions;

public class InvoiceNotFoundException extends Exception {

    public InvoiceNotFoundException(String message) {
        super(message);
    }
}
