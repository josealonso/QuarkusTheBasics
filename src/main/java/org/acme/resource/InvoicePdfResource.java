package org.acme.resource;

import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ExternalContext;
import org.acme.model.User;
import org.acme.service.InvoicePdfService;
import org.acme.service.InvoiceService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.logging.Logger;

@Path("/api/invoices")
public class InvoicePdfResource {
    private static final Logger logger = Logger.getLogger(InvoicePdfResource.class.getName());

    @Inject
    InvoiceService invoiceService;

    @Inject
    InvoicePdfService pdfService;

    @Inject
    FacesContext facesContext;

    @GET
    @Path("/{id}/pdf")
    @Produces("application/pdf")
    public Response getPdf(@PathParam("id") Long id) {
        try {
            ExternalContext externalContext = facesContext.getExternalContext();
            
            // Get user from session
            User sessionUser = (User) externalContext.getSessionMap().get("user");
            if (sessionUser == null) {
                logger.severe("No user in session");
                writeToLog("null user in session");
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("User not logged in")
                        .type(MediaType.TEXT_PLAIN)
                        .build();
            }

            //logger.info("PDF requested for user: " + sessionUser.getEmail());

            var invoice = invoiceService.getInvoiceById(id)
                    .orElseThrow(() -> new NotFoundException("Invoice not found"));

            // Check if user has access to this invoice
            if ( 2 == 3) {  // TODO
                return Response.status(Response.Status.FORBIDDEN)
                        .entity("You don't have permission to access this invoice")
                        .type(MediaType.TEXT_PLAIN)
                        .build();
            }

            byte[] pdfContent = pdfService.generateInvoicePdf(invoice, sessionUser);

            StreamingOutput streamingOutput = output -> {
                output.write(pdfContent);
                output.flush();
            };

            String disposition = externalContext.getRequestParameterMap().get("download") != null ? 
                "attachment" : "inline";

            return Response.ok(streamingOutput)
                    .header("Content-Disposition", disposition + "; filename=\"invoice-" + invoice.getInvoiceNumber() + ".pdf\"")
                    .build();
        } catch (Exception e) {
            logger.severe("Failed to generate PDF: " + e.getMessage());
            writeToLog("Failed to generate PDF: " + e.getMessage());
            return Response.serverError()
                    .entity("Failed to generate PDF: " + e.getMessage())
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteInvoice(@PathParam("id") Long id) {
        try {
            logger.info("Attempting to delete invoice with ID: " + id);
            writeToLog("Delete request received for invoice ID: " + id);
            
            // Get user from session for authorization
            ExternalContext externalContext = facesContext.getExternalContext();
            User sessionUser = (User) externalContext.getSessionMap().get("user");
            if (sessionUser == null) {
                logger.severe("No user in session");
                writeToLog("null user in session during delete attempt");
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(Collections.singletonMap("message", "User not logged in"))
                        .build();
            }
            
            invoiceService.deleteInvoice(id);
            
            logger.info("Successfully deleted invoice with ID: " + id);
            writeToLog("Successfully deleted invoice ID: " + id);
            
            return Response.ok()
                    .entity(Collections.singletonMap("message", "Invoice deleted successfully"))
                    .build();
                    
        } catch (Exception e) {
            String errorMsg = "Failed to delete invoice: " + e.getMessage();
            logger.severe(errorMsg);
            writeToLog("Error: " + errorMsg);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Collections.singletonMap("message", errorMsg))
                    .build();
        }
    }

    private void writeToLog(String message) {
        try {
            Files.write(
                Paths.get("pdf-logs.txt"), message.getBytes(), 
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
            );
        } catch (IOException e) {
            logger.severe("Failed to write to log file: " + e.getMessage());
        }
    }
}
