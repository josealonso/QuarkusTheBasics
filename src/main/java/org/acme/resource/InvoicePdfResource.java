package org.acme.resource;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ExternalContext;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import org.acme.model.Invoice;
import org.acme.model.User;
import org.acme.controller.InvoiceDTO;
import org.acme.service.InvoicePdfService;
import org.acme.service.InvoiceService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;
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

    @Inject
    EntityManager entityManager;  // IMPORTANT

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getInvoice(@PathParam("id") Long id) {
        try {
            ExternalContext externalContext = facesContext.getExternalContext();
            
            // Get user from session
            User sessionUser = (User) externalContext.getSessionMap().get("user");
            if (sessionUser == null) {
                logger.severe("No user in session");
                writeToLog("null user in session");
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(Collections.singletonMap("message", "User not logged in"))
                        .build();
            }

            var invoice = invoiceService.getInvoiceById(id)
                    .orElseThrow(() -> new NotFoundException("Invoice not found"));

            // Check if user has access to this invoice
            if ( 2 == 3) {  // TODO
                return Response.status(Response.Status.FORBIDDEN)
                        .entity(Collections.singletonMap("message", "You don't have permission to access this invoice"))
                        .build();
            }

            return Response.ok(invoice).build();
        } catch (Exception e) {
            logger.severe("Failed to get invoice: " + e.getMessage());
            writeToLog("Failed to get invoice: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Collections.singletonMap("message", "Failed to get invoice: " + e.getMessage()))
                    .build();
        }
    }

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

    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    public Response createInvoice(InvoiceDTO invoiceDTO) {
        try {
            logger.info("Attempting to create invoice");
            writeToLog("Inside InvoicePdfResource.createInvoice(). " + "Create request received");
            writeToLog("Inside InvoicePdfResource.createInvoice(). " + "Invoice data: " + invoiceDTO);
            
            // Get user from session for authorization
            ExternalContext externalContext = facesContext.getExternalContext();
            User sessionUser = (User) externalContext.getSessionMap().get("user");
            if (sessionUser == null) {
                logger.severe("No user in session");
                writeToLog("null user in session during create attempt");
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(Collections.singletonMap("message", "User not logged in"))
                        .build();
            }

            writeToLog("Inside InvoicePdfResource.createInvoice(). " 
                        + "About to call entityManager.find(User.class, sessionUser.getId())");
            
            // IMPORTANT!! DO NOT use a detached User instance from the session
            // Get managed user instance from database
            User managedUser = entityManager.find(User.class, sessionUser.getId());
            if (managedUser == null) {
                logger.severe("User not found in database");
                writeToLog("User not found in database during create attempt");
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(Collections.singletonMap("message", "User not found"))
                        .build();
            }
    
            // Create new invoice entity directly
            Invoice newInvoice = new Invoice(
                null,
                invoiceDTO.getInvoiceNumber(),
                new java.sql.Date(new Date().getTime()),  // Convert util.Date to sql.Date
                invoiceDTO.getInvoiceCustomerName(),
                invoiceDTO.getInvoiceAmount(),
                managedUser
            );
            
            writeToLog("Inside InvoicePdfResource.createInvoice(). " 
                        + "About to call entityManager.persist(newInvoice)");
            // Persist the invoice
            entityManager.persist(newInvoice);
            entityManager.flush();
            
            logger.info("Successfully created invoice");
            writeToLog("Successfully created invoice" + invoiceDTO);
            
            return Response.ok()
                    .entity(Collections.singletonMap("message", "Invoice created successfully"))
                    .build();
                    
        } catch (Exception e) {
            String errorMsg = "Failed to create invoice: " + e.getMessage();
            logger.severe(errorMsg);
            writeToLog("Error: " + errorMsg);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Collections.singletonMap("message", errorMsg))
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

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateInvoice(@PathParam("id") Long id, InvoiceDTO invoiceDTO) {
        try {
            logger.info("Attempting to update invoice with ID: " + id);
            writeToLog("Update request received for invoice ID: " + id + " with data: " + invoiceDTO);
            
            // Get user from session for authorization
            ExternalContext externalContext = facesContext.getExternalContext();
            User sessionUser = (User) externalContext.getSessionMap().get("user");
            if (sessionUser == null) {
                logger.severe("No user in session");
                writeToLog("null user in session during update attempt");
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(Collections.singletonMap("message", "User not logged in"))
                        .build();
            }
            
            // Ensure the path ID matches the invoice ID
            if (!id.equals(invoiceDTO.getId())) {
                logger.warning("Path ID does not match invoice ID");
                writeToLog("Path ID does not match invoice ID");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Collections.singletonMap("message", "Path ID does not match invoice ID"))
                        .build();
            }
            
            InvoiceDTO updatedInvoice = invoiceService.updateInvoice(invoiceDTO);
            
            logger.info("Successfully updated invoice with ID: " + id);
            writeToLog("Successfully updated invoice ID: " + id);
            
            return Response.ok()
                    .entity(updatedInvoice)
                    .build();
                    
        }  /* catch (InvoiceNotFoundException e) {
            String errorMsg = "Invoice not found: " + e.getMessage();
            logger.warning(errorMsg);
            writeToLog("Error: " + errorMsg);
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Collections.singletonMap("message", errorMsg))
                    .build();
        } */
        catch (Exception e) {
            String errorMsg = "Failed to update invoice: " + e.getMessage();
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
                Paths.get("log.txt"), ("\n" + message + "\n").getBytes(), 
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
            );
        } catch (IOException e) {
            logger.severe("Failed to write to log file: " + e.getMessage());
        }
    }
}
