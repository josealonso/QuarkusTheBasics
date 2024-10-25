package org.acme.controller;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.acme.service.Constants.EXCEL_CONTENT_TYPE;
import static org.acme.service.Constants.WORD_CONTENT_TYPE;

@QuarkusTest
class DocumentResourceControllerTest {

    @Test
    void testWordEndpoint() {
        given()
            .when()
            .get("/document/word/")
            .then()
                .statusCode(200)
                .contentType(WORD_CONTENT_TYPE);
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