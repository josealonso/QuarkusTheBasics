package org.acme.controller;

import io.quarkus.logging.Log;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import org.acme.service.DocumentGeneration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

@Path("/hello")
public class GreetingController {

    private DocumentGeneration documentGeneration;

    @GET
    @Path("/word")
    @Produces("application/vnd.openxmlformats-officedocument.wordprocessingml.document")
    public Response hello() {
        FileOutputStream wordFile;
        documentGeneration = new DocumentGeneration();
        try {
            wordFile = documentGeneration.generateWordFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Log.info("================ The document has been generated ================");

        var file = new File("proposal.docx");

        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            byte[] fileContent = new byte[(int) file.length()];
            fileInputStream.read(fileContent);

            return Response.ok(fileContent)
                    .header("Content-Disposition", "attachment; filename=\"proposal.docx\"")
                    .build();
        } catch (IOException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error reading file").build();
        }
    }
    
}
