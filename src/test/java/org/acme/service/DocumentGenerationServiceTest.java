package org.acme.service;

import org.acme.controller.Invoice;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Collectors;

import static org.acme.service.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DocumentGenerationServiceTest {

    private Invoice invoice;

    private XWPFDocument convertFileToXWPFDocument(File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        var xwpfDocument = new XWPFDocument(fis);
        fis.close();
        return xwpfDocument;
    }

    private XSSFWorkbook convertFileToXSSFWorkbook(File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        var xssfDocument = new XSSFWorkbook(fis);
        fis.close();
        return xssfDocument;
    }

    @BeforeAll
    void prepareInvoiceData() {
        invoice = new Invoice(0, "1", "2023-06-01", "John Doe", "100.00");
    }

    @Test
    void testTheWordDocumentHasBeenGenerated() throws IOException {
        new WordDocumentGenerationService().generateWordFile("prueba.docx", invoice);
        var wordDocument = Path.of("prueba.docx").toFile();
        assertTrue(wordDocument.exists());
    }

    @Test
    void testTheGeneratedWordDocumentHasTheExpectedContent() throws IOException {
        var document = new WordDocumentGenerationService().generateWordFile("prueba.docx", invoice);
        var wordDocument = Path.of("prueba.docx").toFile();
        var paragraphs = convertFileToXWPFDocument(wordDocument).getParagraphs();
        document.close();

        XWPFParagraph title = paragraphs.get(TITLE_PARAGRAPH_NUMBER);
        XWPFParagraph subTitle = paragraphs.get(SUBTITLE_PARAGRAPH_NUMBER);
        XWPFRun titleRun = title.getRuns().get(TITLE_PARAGRAPH_NUMBER);

        assertEquals(WORD_DOCUMENT_TITLE, title.getText());
        assertEquals(WORD_TITLE_COLOR, titleRun.getColor());
        assertTrue(titleRun.isBold());
        assertEquals(WORD_TITLE_FONT_FAMILY, titleRun.getFontFamily());
        assertEquals(WORD_TITLE_FONT_SIZE, titleRun.getFontSize());

        assertEquals(WORD_DOCUMENT_SUBTITLE, subTitle.getText());

        // Make sure the document contains the invoice data
        var lines = paragraphs.stream()
                .map(XWPFParagraph::getText)
                .collect(Collectors.joining("\n"));

        assertThat(lines).contains(invoice.getInvoiceDate(), invoice.getInvoiceNumber(),
                invoice.getCustomerName().toUpperCase(), invoice.getAmount());

        paragraphs.forEach(paragraph -> System.out.println("Line: " + paragraph.getText()));
    }

    @Test
    void testTheExcelDocumentHasBeenGenerated() throws IOException {
        new ExcelDocumentGenerationService().generateExcelFile("prueba.xlsx");
        var excelDocument = Path.of("prueba.xlsx").toFile();
        assertTrue(excelDocument.exists());
    }

    @Test
    void testTheGeneratedExcelDocumentHasTheExpectedContent() throws IOException {
        var document = new ExcelDocumentGenerationService().generateExcelFile("prueba.xlsx");
        var excelDocument = Path.of("prueba.xlsx").toFile();
        var workbook = convertFileToXSSFWorkbook(excelDocument).getCTWorkbook();
        document.close();

        var firstSheet = workbook.getSheets().getSheetList().get(0);

        assertEquals(1, workbook.getSheets().getSheetList().size());
        assertEquals(EXCEL_FIRST_SHEET, firstSheet.getName());
//        assertEquals(EXCEL_FIRST_CELL, firstSheet.getCells().get(0).getStringCellValue());   // TODO
    }

}


















