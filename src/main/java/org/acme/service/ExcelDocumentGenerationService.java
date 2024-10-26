package org.acme.service;

import jakarta.enterprise.context.RequestScoped;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;

import static org.acme.service.Constants.*;

@RequestScoped
public class ExcelDocumentGenerationService {

//	private DocumentResourceController documentResourceController;

	public ExcelDocumentGenerationService() {
//		documentResourceController = DocumentResourceController(this);
	}

	/****************************************************************************************************/
	/*************************** This will be the Excel microservice  ***********************************/
	/****************************************************************************************************/

	public FileOutputStream generateExcelFile(String excelFileName) throws IOException {
		Workbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet(EXCEL_FIRST_SHEET);
		Row row = sheet.createRow(0);
		// set row styles
		row.setHeight((short) 500);
		Cell cell = row.createCell(0);
		cell.setCellValue(EXCEL_FIRST_CELL);
		return composeExcelFile(workbook, excelFileName);
	}

	private FileOutputStream composeExcelFile(Workbook document, String excelFileName) throws IOException {
		FileOutputStream outputStream = new FileOutputStream(excelFileName);
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















