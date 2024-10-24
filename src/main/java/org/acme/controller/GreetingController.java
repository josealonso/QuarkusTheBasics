package org.acme.controller;

import io.quarkus.logging.Log;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.acme.service.DocumentGeneration;

import java.io.IOException;

@Path("/hello")
public class GreetingController {

    private DocumentGeneration documentGeneration;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        documentGeneration = new DocumentGeneration();
        try {
            documentGeneration.generateWordFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Log.info("================ The document has been generated ================");
        return "Hello from UbiNova";
    }
}
