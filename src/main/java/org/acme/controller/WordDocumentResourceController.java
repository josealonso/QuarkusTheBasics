package org.acme.controller;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import org.acme.Utilities;
import org.acme.service.WordDocumentGenerationService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import static org.acme.service.Constants.WORD_CONTENT_TYPE;
import static org.acme.service.Constants.WORD_DOCUMENT_NAME;

/*****************************************************************************
 * The default scope in Quarkus is designed to be safe and flexible for most scenarios.
 * @ApplicationScoped annotation requires thread-safe implementation if you maintain any state.
 */
@ApplicationScoped
@Path("/document")
public class WordDocumentResourceController {

    private final WordDocumentGenerationService documentGenerationService;
    private final Utilities utilities = new Utilities();

    public WordDocumentResourceController(WordDocumentGenerationService documentGenerationService) {
        this.documentGenerationService = documentGenerationService;
    }

    @GET
    @Path("/word")
    @Produces(WORD_CONTENT_TYPE)
    public Response getWordDocument() throws IOException {

        var wordFileName = createFileNameWithTimestampSuffix();

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

    private String createFileNameWithTimestampSuffix() {
        return utilities.createFileNameWithTimestampSuffix(WORD_DOCUMENT_NAME) + ".docx";
    }

}








