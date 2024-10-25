package org.acme.service;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.*;

import java.io.FileOutputStream;
import java.io.IOException;

import static org.acme.service.Constants.*;

@ApplicationScoped
public class DocumentGenerationService {

	private final XWPFDocument document;

	public DocumentGenerationService() {
		document = new XWPFDocument();
	}

    /**
     * Generates a Word document and returns it as a FileOutputStream.
     * The document includes the title and subtitle centered and in color,
     * followed by a paragraph with the given content.
     * This method works for .docx files (Word 2007 and later).
	 * For older .doc files, the HWPFDocument type must be used instead of the XWPFDocument one.
	 *
     * @return the FileOutputStream containing the generated document
     * @throws IOException if the document could not be generated
     */
    public FileOutputStream generateWordFile() throws IOException {
	    formatTitle();
		formatSubtitle();
		String content = WORD_FIRST_LINE;
	    var finalDocument = formatParagraphs(content);
	    return composeWordFile(finalDocument);
	}

    /**
     * Formats the title of the Word document by creating a centered paragraph
     * with specified text, color, boldness, font family, and font size.
	 *
     * @return the XWPFDocument with the formatted title paragraph
     */
	private XWPFDocument formatTitle() {
		XWPFParagraph titleParagraph = document.createParagraph();
		titleParagraph.setAlignment(ParagraphAlignment.CENTER);
		XWPFRun titleRun = titleParagraph.createRun();
		titleRun.setText(WORD_DOCUMENT_TITLE);
		titleRun.setColor(WORD_TITLE_COLOR);
		titleRun.setBold(true);
		titleRun.setFontFamily(WORD_TITLE_FONT_FAMILY);
		titleRun.setFontSize(WORD_TITLE_FONT_SIZE);
		return document;
	}

	private XWPFDocument formatSubtitle() {
		XWPFParagraph subTitleParagraph = document.createParagraph();
		subTitleParagraph.setAlignment(ParagraphAlignment.CENTER);
		XWPFRun subTitleRun = subTitleParagraph.createRun();
		subTitleRun.setText(WORD_DOCUMENT_SUBTITLE);
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

	private FileOutputStream composeWordFile(XWPFDocument document) throws IOException {
		FileOutputStream outputStream = new FileOutputStream(WORD_DOCUMENT_NAME);
        try {
            document.write(outputStream);
			outputStream.close();
			document.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
	    }
		return outputStream;
	}

	/****************************************************************************************************/
	/*************************** This will be the Excel microservice  ***********************************/
	/****************************************************************************************************/

	public FileOutputStream generateExcelFile() throws IOException {
		Workbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet(EXCEL_FIRST_SHEET);
		Row row = sheet.createRow(0);
		// set row styles
		row.setHeight((short) 500);
		Cell cell = row.createCell(0);
		cell.setCellValue(EXCEL_FIRST_CELL);
		return composeExcelFile(workbook);
	}

	private FileOutputStream composeExcelFile(Workbook document) throws IOException {
		FileOutputStream outputStream = new FileOutputStream(EXCEL_DOCUMENT_NAME);
		try {
			document.write(outputStream);
			outputStream.close();
			document.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return outputStream;
	}

}















