package org.acme.controller;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;
import net.sf.jasperreports.engine.JRException;
import org.acme.Utilities;
import org.acme.service.PdfDocumentGenerationService;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import static org.acme.service.Constants.PDF_DOCUMENT_NAME;

/*****************************************************************************
 * The default scope in Quarkus is designed to be safe and flexible for most scenarios.
 * @ApplicationScoped annotation requires thread-safe implementation if you maintain any state.
 */
@ApplicationScoped
@Path("/document")
public class PdfDocumentResourceController {

    private final PdfDocumentGenerationService documentGenerationService;
    private final Utilities utilities = new Utilities();

    public PdfDocumentResourceController(PdfDocumentGenerationService documentGenerationService) {
        this.documentGenerationService = documentGenerationService;
    }

    @GET
    @Path("/pdf")
    @Produces("application/pdf")
    public Response getPdf() throws JRException {

        var pdfFileName = createFileNameWithTimestampSuffix();
        var jasperReportTemplate = utilities.getJasperReportTemplate();

        try (FileOutputStream pdfFile = documentGenerationService.generatePdfFile(PDF_DOCUMENT_NAME, jasperReportTemplate)) {
            Log.info("================ The pdf document has been generated ================");
        } catch (IOException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error generating Word file")
                    .build();
        }

        // Return the PDF as a response
        ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
        StreamingOutput streamingOutput = output -> {
             output.write(pdfOutputStream.toByteArray());
             output.flush();
        };

        return Response
                .ok(streamingOutput)
                .header("Content-Disposition", "inline; filename=\"" + pdfFileName + "\"")
                .build();
    }

    private String createFileNameWithTimestampSuffix() {
        return utilities.createFileNameWithTimestampSuffix(PDF_DOCUMENT_NAME) + ".pdf";
    }

//    @GET
//    @Path("/pdf")
//    @Produces(EXCEL_CONTENT_TYPE)
//    public Response getExcelDocument() throws IOException {
//
//        var excelFileName = createFileNameWithTimestampSuffix();
//
//        try (FileOutputStream excelFile = documentGenerationService.generatePdfFile(excelFileName)) {
//            Log.info("================ The excel document has been generated ================");
//        }
//
//        var file = new File(excelFileName);
//        try (FileInputStream fileInputStream = new FileInputStream(file)) {
//            byte[] fileContent = new byte[(int) file.length()];
//            fileInputStream.read(fileContent);
//
////        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {  // this code block generated an empty file
////            byte[] excelBytes = outputStream.toByteArray();
//
//            return Response.ok(fileContent)
//                    .header("Content-Disposition", "attachment; filename=\"" + excelFileName + "\"")
//                    .build();
//        } catch (IOException e) {
//            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error generating Excel file")
//                    .build();
//        }
//    }
//
//    private String createFileNameWithTimestampSuffix() {
//        return utilities.createFileNameWithTimestampSuffix(EXCEL_DOCUMENT_NAME) + ".xlsx";
//    }

}








