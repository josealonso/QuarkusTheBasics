package org.acme.service;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;

import static org.acme.service.Constants.*;
import static org.junit.jupiter.api.Assertions.*;

class DocumentGenerationServiceTest {

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

    @Test
    void testTheWordDocumentHasBeenGenerated() throws IOException {
        new DocumentGenerationService().generateWordFile("prueba");
        var wordDocument = Path.of("prueba").toFile();
        assertTrue(wordDocument.exists());
    }

    @Test
    void testTheGeneratedWordDocumentHasTheExpectedContent() throws IOException {
        var document = new DocumentGenerationService().generateWordFile("prueba");
        var wordDocument = Path.of("prueba").toFile();
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

        assertEquals(WORD_FIRST_LINE, paragraphs.get(FIRST_LINE_PARAGRAPH_NUMBER).getText());
    }

    @Test
    void testTheExcelDocumentHasBeenGenerated() throws IOException {
        new DocumentGenerationService().generateExcelFile("prueba");
        var excelDocument = Path.of(EXCEL_DOCUMENT_NAME).toFile();
        assertTrue(excelDocument.exists());
    }

    @Test
    void testTheGeneratedExcelDocumentHasTheExpectedContent() throws IOException {
        var document = new DocumentGenerationService().generateExcelFile("prueba");
        var excelDocument = Path.of(EXCEL_DOCUMENT_NAME).toFile();
        var workbook = convertFileToXSSFWorkbook(excelDocument).getCTWorkbook();
        document.close();

        var firstSheet = workbook.getSheets().getSheetList().get(0);

        assertEquals(1, workbook.getSheets().getSheetList().size());
        assertEquals(EXCEL_FIRST_SHEET, firstSheet.getName());
//        assertEquals(EXCEL_FIRST_CELL, firstSheet.getCells().get(0).getStringCellValue());   // TODO
    }

}


















