package org.acme.service;

import jakarta.enterprise.context.RequestScoped;
import org.acme.controller.Invoice;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import java.io.FileOutputStream;
import java.io.IOException;

import static org.acme.service.Constants.*;

@RequestScoped
public class WordDocumentGenerationService {

    public static final int RIGHT_ALIGNMENT = 11;
	public static final int LEFT_ALIGNMENT = 10;
	public static final int CENTER_ALIGNMENT = 1;
	public static final int JUSTIFY_ALIGNMENT = 3;
	public static final String EMPTY_STRING = "";
//	START(1),
//	CENTER(2),
//	END(3),
//	BOTH(4),
//	MEDIUM_KASHIDA(5),
//	DISTRIBUTE(6),
//	NUM_TAB(7),
//	HIGH_KASHIDA(8),
//	LOW_KASHIDA(9),
//	THAI_DISTRIBUTE(10),
//	LEFT(11),
//	RIGHT(12);


	public WordDocumentGenerationService() {
//		documentResourceController = DocumentResourceController(this);
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
    public FileOutputStream generateWordFile(String wordFileName, Invoice invoice) throws IOException {
        XWPFDocument document = new XWPFDocument();
		formatTitle(document);
		formatSubtitle(document);
        var finalDocument = formatParagraphs(document, invoice);
	    return composeWordFile(finalDocument, wordFileName);
	}

	private FileOutputStream composeWordFile(XWPFDocument document, String wordFileName) throws IOException {
		FileOutputStream outputStream = new FileOutputStream(wordFileName);
		try {
			document.write(outputStream);
			outputStream.close();
			document.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return outputStream;
	}

	/**
     * Formats the title of the Word document by creating a centered paragraph
     * with specified text, color, boldness, font family, and font size.
	 *
     * @return the XWPFDocument with the formatted title paragraph
     */
	private XWPFDocument formatTitle(XWPFDocument document) {
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

	private XWPFDocument formatSubtitle(XWPFDocument document) {
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

	private XWPFDocument formatParagraphs(XWPFDocument document, Invoice invoice) {
		MyDocString docString = new MyDocString(document, EMPTY_STRING);
		docString.addLine(invoice.invoiceDate())
				.addLine(EMPTY_STRING)
				.addLine("INVOICE #" + invoice.invoiceNumber())
				.addLine(EMPTY_STRING)
				.addLine(EMPTY_STRING)
				.addLine(invoice.customerName().toUpperCase() + " has paid the amount of " +
						invoice.amount() + " euros");
		return docString.getDocument();
	}

	// Unused
	private XWPFDocument composeLine(XWPFDocument document, String content, int paragraphAlignment) {
		var line = document.createParagraph();
		line.setAlignment(ParagraphAlignment.valueOf(paragraphAlignment));
		var lineRun = line.createRun();
		lineRun.setText(content);
		return document;
	}

}

















