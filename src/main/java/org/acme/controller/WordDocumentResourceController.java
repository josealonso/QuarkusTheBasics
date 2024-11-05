package org.acme.controller;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.Utilities;
import org.acme.service.WordDocumentGenerationService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;

import static org.acme.service.Constants.WORD_DOCUMENT_NAME;

/*****************************************************************************
 * The default scope in Quarkus is designed to be safe and flexible for most scenarios.
 * @ApplicationScoped annotation requires thread-safe implementation if you maintain any state.
 */
@ApplicationScoped
@Path("/document/word")
public class WordDocumentResourceController {

    private final WordDocumentGenerationService documentGenerationService;
    private final Utilities utilities = new Utilities();

    public WordDocumentResourceController(WordDocumentGenerationService documentGenerationService) {
        this.documentGenerationService = documentGenerationService;
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
        public Response submitForm(@FormParam("invoiceNumber") String invoiceNumber,
                               @FormParam("invoiceDate") String invoiceDate,
                               @FormParam("customerName") String customerName,
                               @FormParam("amount") String amount) throws IOException {

            writeLogs("AAAAA - Invoice number: " + invoiceNumber + "\n" +
                "AAAAA - Invoice date: " + invoiceDate + "\n" +
                "AAAAA - Customer name: " + customerName + "\n" +
                "AAAAA - Amount: " + amount);

        if (invoiceNumber == null || invoiceDate == null || customerName == null || amount == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invoice number is required").build();
        }

        var wordFileName = createFileNameWithTimestampSuffix();

        var invoice = Invoice.builder()
                .invoiceNumber(invoiceNumber)
                .invoiceDate(invoiceDate)
                .customerName(customerName)
                .amount(amount)
                .build();

        try (FileOutputStream wordFile = documentGenerationService.generateWordFile(wordFileName, invoice)) {
            Log.info("================ The word document has been generated ================");
        }

        var file = new File(wordFileName);
        try (FileInputStream fileInputStream = new FileInputStream(file)) {   // this code block caused an exception
            byte[] fileContent = new byte[(int) file.length()];
            fileInputStream.read(fileContent);   // This line is needed

            return Response.created(URI.create(wordFileName))
                    .entity(fileContent)
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

    public void writeLogs(String content) {
//        String content = "Hello, this is some text written to a file.";
        try {
            Files.writeString(java.nio.file.Path.of("logs.txt"), content);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}








