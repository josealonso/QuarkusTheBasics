package org.acme.service;

import jakarta.enterprise.context.RequestScoped;
import org.acme.controller.Invoice;
import org.apache.poi.xwpf.usermodel.*;

import java.io.FileOutputStream;
import java.io.IOException;

import static org.acme.service.Constants.*;

@RequestScoped
public class WordDocumentGenerationService {

//	private DocumentResourceController documentResourceController;

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
		var document1 = composeLine(document, invoice.getInvoiceDate());
		var document2 = composeLine(document1, invoice.getInvoiceNumber());
		var document3 = composeLine(document2, invoice.getCustomerName());
        return composeLine(document3, invoice.getAmount());
	}

	private XWPFDocument composeLine(XWPFDocument document, String content) {
		var line = document.createParagraph();
		line.setAlignment(ParagraphAlignment.BOTH);
		var lineRun = line.createRun();
		lineRun.setText((WORD_FIRST_LINE + content));
		return document;
	}

}

















