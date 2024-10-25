package org.acme.controller;

import io.quarkus.logging.Log;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import org.acme.service.DocumentGeneration;

import java.io.*;

import static org.acme.service.Constants.EXCEL_DOCUMENT_NAME;
import static org.acme.service.Constants.WORD_DOCUMENT_NAME;

@Path("/document")
public class DocumentResource {

    private DocumentGeneration documentGeneration;

//    public DocumentResource(DocumentGeneration documentGeneration) {
//        this.documentGeneration = new DocumentGeneration();
//    }

    @GET
    @Path("/word")
    @Produces("application/vnd.openxmlformats-officedocument.wordprocessingml.document")
    public Response getWordDocument() {
        FileOutputStream wordFile;
        documentGeneration = new DocumentGeneration();
        try {
            wordFile = documentGeneration.generateWordFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Log.info("================ The word document has been generated ================");

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
    public Response getExcelDocument() {
        FileOutputStream excelFile;
        try {
            excelFile = documentGeneration.generateExcelFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Log.info("================ The excel document has been generated ================");

        var file = new File(WORD_DOCUMENT_NAME);

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
