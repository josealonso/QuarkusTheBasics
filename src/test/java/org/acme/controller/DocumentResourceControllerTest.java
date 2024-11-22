package org.acme.controller;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.ValidatableResponse;
import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.time.LocalDate;

import static io.restassured.RestAssured.given;
import static org.acme.service.Constants.EXCEL_CONTENT_TYPE;

@QuarkusTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DocumentResourceControllerTest {

    private InvoiceDTO invoice2;

    @BeforeAll
    void prepareInvoiceData() {
        invoice2 = new InvoiceDTO(1L, "1", "Jose", String.valueOf(LocalDate.now()), "256.25");

    }

    @Test
    void testWordEndpoint() {

        ValidatableResponse response =
                given()
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .formParam("invoiceNumber", invoice2.getInvoiceNumber())
                        .formParam("invoiceDate", invoice2.getInvoiceDate())
                        .formParam("customerName", invoice2.getInvoiceCustomerName())
                        .formParam("amount", invoice2.getInvoiceAmount())
                        .when()
                        .post("/document/word/")
                        .then()
                        .statusCode(201);

        // The response is a docs document, so it is not possible to check the content
        // TODO   assert the generated file has the correct name

        System.out.println(" ============ Response: " + response.extract().asPrettyString());
    }

    @Test
    void testExcelEndpoint() {
        given()
                .formParam("invoiceNumber", invoice2.getInvoiceNumber())
                .formParam("invoiceDate", invoice2.getInvoiceDate())
                .formParam("customerName", invoice2.getInvoiceCustomerName())
                .formParam("amount", invoice2.getInvoiceAmount())
                .when()
                .get("/document/excel/")
                .then()
                .statusCode(200)
                .contentType(EXCEL_CONTENT_TYPE);
    }

}
