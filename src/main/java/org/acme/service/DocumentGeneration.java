package org.acme.service;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static org.acme.service.Constants.*;

public class DocumentGeneration {

	XWPFDocument document;
	String title;
	String subtitle;

	public DocumentGeneration() {
		document = new XWPFDocument();
	}

    DocumentGeneration(String title, String subtitle, String content) {
		document = new XWPFDocument();
        this.title = title;
        this.subtitle = subtitle;
//        this.content = content;
    }

    public FileOutputStream generateWordFile() throws IOException {
	    formatTitle();
		formatSubtitle();
		String content = FIRST_LINE;
	    var finalDocument = formatParagraphs(content);
	    return generateWordFile(finalDocument);
	}

	private XWPFDocument formatTitle() {
		XWPFParagraph titleParagraph = document.createParagraph();
		titleParagraph.setAlignment(ParagraphAlignment.CENTER);
		XWPFRun titleRun = titleParagraph.createRun();
		titleRun.setText(DOCUMENT_TITLE);
		titleRun.setColor(TITLE_COLOR);
		titleRun.setBold(true);
		titleRun.setFontFamily(TITLE_FONT_FAMILY);
		titleRun.setFontSize(TITLE_FONT_SIZE);
		return document;
	}

	private XWPFDocument formatSubtitle() {
		XWPFParagraph subTitleParagraph = document.createParagraph();
		subTitleParagraph.setAlignment(ParagraphAlignment.CENTER);
		XWPFRun subTitleRun = subTitleParagraph.createRun();
		subTitleRun.setText(subtitle);
		subTitleRun.setColor("00CC44");
		subTitleRun.setFontFamily("Courier");
		subTitleRun.setFontSize(16);
		subTitleRun.setTextPosition(20);
		subTitleRun.setUnderline(UnderlinePatterns.DOT_DOT_DASH);
		return document;
	}

	private XWPFDocument formatParagraphs(String content) {
		XWPFParagraph firstParagraph = document.createParagraph();
		firstParagraph.setAlignment(ParagraphAlignment.BOTH);
		XWPFRun firstParagraphRun = firstParagraph.createRun();
		firstParagraphRun.setText(content);
		return document;
	}

	private FileOutputStream generateWordFile(XWPFDocument document) throws IOException {
		FileOutputStream out = new FileOutputStream(new File(WORD_DOCUMENT_NAME));
        try {
            document.write(out);
			out.close();
			document.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
	    }
		return out;
	}

	/****************************************************************************************************/
	/*************************** This will be the Excel microservice  ***********************************/
	/****************************************************************************************************/

	public FileOutputStream generateExcelFile() throws IOException {
		Workbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet("Sample Tab");
		Row row = sheet.createRow(0);
		// set row styles
		row.setHeight((short) 500);
		Cell cell = row.createCell(0);
		cell.setCellValue("Hello, Excel!");
		return generateFinalExcelFile(workbook);
	}

	private FileOutputStream generateFinalExcelFile(Workbook document) throws IOException {
		FileOutputStream out = new FileOutputStream(new File(EXCEL_DOCUMENT_NAME));
		try {
			document.write(out);
			out.close();
			document.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return out;
	}

}















