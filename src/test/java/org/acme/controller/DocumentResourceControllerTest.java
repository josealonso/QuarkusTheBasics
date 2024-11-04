package org.acme.controller;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import jakarta.ws.rs.core.MediaType;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.time.LocalDate;

import static io.restassured.RestAssured.given;
import static org.acme.service.Constants.EXCEL_CONTENT_TYPE;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DocumentResourceControllerTest {

    private Invoice invoice;

    @BeforeAll
    void prepareInvoiceData() {
        invoice = Invoice.builder()
                .invoiceNumber(String.valueOf(1))
                .invoiceDate(String.valueOf(LocalDate.of(2023, 8, 1)))
                .customerName("user")
                .amount(String.valueOf(145.00))
                .build();
    }

    @Test
    void testWordEndpoint() {
        // Create JSON payload
        JSONObject requestParams = new JSONObject();
        requestParams.put("invoiceNumber", invoice.getInvoiceNumber());
        requestParams.put("invoiceDate", invoice.getInvoiceDate());
        requestParams.put("customerName", invoice.getCustomerName());
        requestParams.put("amount", invoice.getAmount());

        // Create request specification
        RequestSpecification requestSpecification = given()
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(requestParams.toJSONString());

        Response response = requestSpecification
                .post("/document/word/");

        assertEquals(201, response.getStatusCode());
        // TODO   assert the generated file has the correct name

        System.out.println(" ============ Response: " + response.asPrettyString());
    }

    @Test
    void testExcelEndpoint() {
        given()
                .when()
                .get("/document/excel/")
                .then()
                .statusCode(200)
                .contentType(EXCEL_CONTENT_TYPE);
    }

}
