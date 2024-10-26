package org.acme.controller;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import org.acme.Utilities;
import org.acme.service.ExcelDocumentGenerationService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import static org.acme.service.Constants.EXCEL_CONTENT_TYPE;
import static org.acme.service.Constants.EXCEL_DOCUMENT_NAME;

/*****************************************************************************
 * The default scope in Quarkus is designed to be safe and flexible for most scenarios.
 * @ApplicationScoped annotation requires thread-safe implementation if you maintain any state.
 */
@ApplicationScoped
@Path("/document")
public class ExcelDocumentResourceController {

    private final ExcelDocumentGenerationService documentGenerationService;
    private final Utilities utilities = new Utilities();

    public ExcelDocumentResourceController(ExcelDocumentGenerationService documentGenerationService) {
        this.documentGenerationService = documentGenerationService;
    }

    @GET
    @Path("/excel")
    @Produces(EXCEL_CONTENT_TYPE)
    public Response getExcelDocument() throws IOException {

        var excelFileName = createFileNameWithTimestampSuffix();

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

    private String createFileNameWithTimestampSuffix() {
        return utilities.createFileNameWithTimestampSuffix(EXCEL_DOCUMENT_NAME) + ".xlsx";
    }

}








