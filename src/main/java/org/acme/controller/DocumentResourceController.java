package org.acme.controller;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import org.acme.service.DocumentGenerationService;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.acme.service.Constants.*;

@ApplicationScoped
@Path("/document")
public class DocumentResourceController {

    private final DocumentGenerationService documentGenerationService;

    public DocumentResourceController(DocumentGenerationService documentGenerationService) {
        this.documentGenerationService = documentGenerationService;
    }

    @GET
    @Path("/word")
    @Produces(WORD_CONTENT_TYPE)
    public Response getWordDocument() throws IOException {

        var wordFileName = createFileNameWithTimestampSuffix(WORD_DOCUMENT_NAME) + ".docx";

        try (FileOutputStream wordFile = documentGenerationService.generateWordFile(wordFileName)) {
            Log.info("================ The word document has been generated ================");
        }

        var file = new File(wordFileName);
        try (FileInputStream fileInputStream = new FileInputStream(file)) {   // this code block caused an exception
            byte[] fileContent = new byte[(int) file.length()];
            fileInputStream.read(fileContent);   // This line is needed

            return Response.ok(fileContent)
                    .header("Content-Disposition", "attachment; filename=\"" + wordFileName + "\"")
                    .build();
        } catch (IOException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error generating Word file")
                    .build();
        }
    }

    @GET
    @Path("/excel")
    @Produces(EXCEL_CONTENT_TYPE)
    public Response getExcelDocument() throws IOException {

        var excelFileName = createFileNameWithTimestampSuffix(EXCEL_DOCUMENT_NAME) + ".xlsx";

        try (FileOutputStream excelFile = documentGenerationService.generateExcelFile(excelFileName)) {
            Log.info("================ The excel document has been generated ================");
        }

        var file = new File(excelFileName);
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            byte[] fileContent = new byte[(int) file.length()];
            fileInputStream.read(fileContent);

//        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {  // this code block generated an empty file
//            byte[] excelBytes = outputStream.toByteArray();

            return Response.ok(fileContent)
                    .header("Content-Disposition", "attachment; filename=\"" + excelFileName + "\"")
                    .build();
        } catch (IOException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error generating Excel file")
                    .build();
        }
    }

    private String createFileNameWithTimestampSuffix(String baseFileName) {
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timestamp = dateFormat.format(now);
        return baseFileName + "_" + timestamp;
    }

}








