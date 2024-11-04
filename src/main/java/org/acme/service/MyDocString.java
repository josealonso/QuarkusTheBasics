package org.acme.service;

import org.apache.poi.xwpf.usermodel.XWPFDocument;

/**
 * This class has been created to have a fluent API for composing Word documents.
 *
 * @return the value of the document
 */
public class MyDocString {

    private XWPFDocument document;
    private String value;

    public MyDocString(XWPFDocument document,String value) {
        this.document = document;
        this.value = value;
    }

    public MyDocString addLine(String value) {
        this.value += value;
        document.createParagraph().createRun().setText(value);
        return this;
    }

    public String getValue() {
        return value;
    }

    public XWPFDocument getDocument() {
        return document;
    }
}
