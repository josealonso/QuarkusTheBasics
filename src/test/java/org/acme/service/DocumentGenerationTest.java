package org.acme.service;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.acme.service.Constants.*;
import static org.junit.jupiter.api.Assertions.*;

class DocumentGenerationTest {

    List<XWPFParagraph> paragraphs;

    @BeforeEach
    void setUp() throws IOException {
        var document = new DocumentGeneration().generateWordFile();
        XWPFDocument doc = new XWPFDocument(OPCPackage.create(document));
        paragraphs = doc.getParagraphs();
        doc.close();
    }

    @Test
    void testGenerateDocument() throws IOException {

        XWPFParagraph title = paragraphs.get(0);
        XWPFRun titleRun = title.getRuns().get(0);
        assertEquals(DOCUMENT_TITLE, title.getText());
        assertEquals(TITLE_COLOR, titleRun.getColor());
        assertTrue(titleRun.isBold());
        assertEquals(TITLE_FONT_FAMILY, titleRun.getFontFamily());
        assertEquals(TITLE_FONT_SIZE, titleRun.getFontSize());

//        assertEquals(SUBTITLE, paragraphs.get(1).getText());
        assertEquals(FIRST_LINE, paragraphs.get(3).getText());
    }

}

