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

    private Invoice invoice2;

    @BeforeAll
    void prepareInvoiceData() {
        invoice2 = Invoice.builder()
                .invoiceNumber(String.valueOf(1))
                .invoiceDate(String.valueOf(LocalDate.of(2023, 8, 1)))
                .customerName("user")
                .amount(String.valueOf(145.00))
                .build();
    }

    @Test
    void testWordEndpoint() {

        ValidatableResponse response =
                given()
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .formParam("invoiceNumber", invoice2.invoiceNumber())
                        .formParam("invoiceDate", invoice2.invoiceDate())
                        .formParam("customerName", invoice2.customerName())
                        .formParam("amount", invoice2.amount())
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
                .formParam("invoiceNumber", invoice2.invoiceNumber())
                .formParam("invoiceDate", invoice2.invoiceDate())
                .formParam("customerName", invoice2.customerName())
                .formParam("amount", invoice2.amount())
                .when()
                .get("/document/excel/")
                .then()
                .statusCode(200)
                .contentType(EXCEL_CONTENT_TYPE);
    }

}
