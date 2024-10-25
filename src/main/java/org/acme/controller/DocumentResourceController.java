package org.acme.controller;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import org.acme.service.DocumentGenerationService;

import java.io.*;

import static org.acme.service.Constants.EXCEL_DOCUMENT_NAME;
import static org.acme.service.Constants.WORD_DOCUMENT_NAME;

@ApplicationScoped
@Path("/document")
public class DocumentResourceController {

    private final DocumentGenerationService documentGenerationService;

    public DocumentResourceController(DocumentGenerationService documentGenerationService) {
        this.documentGenerationService = documentGenerationService;
    }

    @GET
    @Path("/word")
    @Produces("application/vnd.openxmlformats-officedocument.wordprocessingml.document")
    public Response getWordDocument() throws IOException {

        try (FileOutputStream wordFile = documentGenerationService.generateWordFile()) {
            Log.info("================ The word document has been generated ================");
        }

        var file = new File(WORD_DOCUMENT_NAME);

        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            byte[] fileContent = new byte[(int) file.length()];
            fileInputStream.read(fileContent);

            return Response.ok(fileContent)
                    .header("Content-Disposition", "attachment; filename=\"" + WORD_DOCUMENT_NAME + "\"")
                    .build();
        } catch (IOException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error generating Word file").build();
        }
    }

    @GET
    @Path("/excel")
    @Produces("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public Response getExcelDocument() throws IOException {

        try (FileOutputStream excelFile = documentGenerationService.generateExcelFile()) {
            Log.info("================ The excel document has been generated ================");
        }

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            byte[] excelBytes = outputStream.toByteArray();

            return Response.ok(excelBytes)
                    .header("Content-Disposition", "attachment; filename=\"" + EXCEL_DOCUMENT_NAME + "\"")
                    .build();
        } catch (IOException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error generating Excel file").build();
        }
    }

}
