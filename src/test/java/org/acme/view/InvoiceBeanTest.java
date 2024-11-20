package org.acme.view;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@QuarkusTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class InvoiceBeanTest {

    @Inject
    private Validator validator;

    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();

    @InjectMock
    private InvoiceBean invoiceBean;

    @BeforeEach
    void setUp() {
        validator = factory.getValidator();
        invoiceBean = new InvoiceBean();
    }

    /*********************** Invoice number field ************************************/

    @Test
    void testValidInvoiceNumber() {
        invoiceBean.setInvoiceNumber("12");
        var violations = validator.validateProperty(invoiceBean, "invoiceNumber");
        violations.forEach(violation ->
                System.out.println(" ===================  " + violation.getMessage()));
        assertThat(violations).isEmpty();
    }

    @Test
    void testEmptyInvoiceNumber() {
        invoiceBean.setInvoiceNumber("");
        var violations = validator.validateProperty(invoiceBean, "invoiceNumber");
        violations.forEach(violation ->
                System.out.println(" ===================  " + violation.getMessage()));
        assertThat(violations).hasSize(2);
    }

    @Test
    void testNullInvoiceNumber() {
        invoiceBean.setInvoiceNumber(null);
        var violations = validator.validateProperty(invoiceBean, "invoiceNumber");
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Invoice Number cannot not be empty");
    }

    @Test
    void testNonDigitInvoiceNumber() {
        invoiceBean.setInvoiceNumber("12a3");
        var violations = validator.validateProperty(invoiceBean, "invoiceNumber");
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Invoice number must contain only digits");
    }

    @Test
    void testInvoiceNumberGetterAndSetter() {
        String testValue = "456";
        invoiceBean.setInvoiceNumber(testValue);
        assertThat(invoiceBean.getInvoiceNumber()).isEqualTo(testValue);
    }

    @Test
    void testGetInvoiceNumberAsInteger() {
        invoiceBean.setInvoiceNumber("789");
        assertThat(invoiceBean.getInvoiceNumberAsInteger()).isEqualTo(789);
    }

    /********************** Invoice date field ************************************/

    @Test
    void testValidInvoiceDate() {
        invoiceBean.setInvoiceDate(LocalDate.of(2023, 8, 1));
        var violations = validator.validateProperty(invoiceBean, "invoiceDate");
        assertThat(violations).isEmpty();
    }

    @Test
    void testEmptyInvoiceDate() {
        var violations = validator.validateProperty(invoiceBean, "invoiceDate");
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Invoice Date cannot be empty");
        assertThatThrownBy(() -> LocalDate.parse(""))
            .isInstanceOf(DateTimeParseException.class);
    }

    @Test
    void testNullInvoiceDate() {
        invoiceBean.setInvoiceDate(null);
        var violations = validator.validateProperty(invoiceBean, "invoiceDate");
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Invoice Date cannot be empty");
    }

    @Test
    void testInvoiceDateGetterAndSetter() {
        LocalDate testDate = LocalDate.of(2023, 8, 1);
        invoiceBean.setInvoiceDate(testDate);
        assertThat(invoiceBean.getInvoiceDate()).isEqualTo(testDate);
    }

    /********************** Customer name field ************************************/

    @Test
    void testValidCustomerName() {
        invoiceBean.setCustomerName("John Doe");
        var violations = validator.validateProperty(invoiceBean, "customerName");
        assertThat(violations).isEmpty();
    }

    @Test
    void testEmptyCustomerName() {
        invoiceBean.setCustomerName("");
        var violations = validator.validateProperty(invoiceBean, "customerName");
        violations.forEach(violation -> System.out.println(violation.getMessage()));
        assertThat(violations).hasSize(2);
    }

    @Test
    void testNullCustomerName() {
        invoiceBean.setCustomerName(null);
        var violations = validator.validateProperty(invoiceBean, "customerName");
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Customer name cannot be empty");
    }

    @Test
    void testInvalidCustomerName() {
        invoiceBean.setCustomerName("John Doe123");
        var violations = validator.validateProperty(invoiceBean, "customerName");
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Customer name must contain only letters and spaces");
    }

    @Test
    void testCustomerNameGetterAndSetter() {
        String testValue = "Jane Doe";
        invoiceBean.setCustomerName(testValue);
        assertThat(invoiceBean.getCustomerName()).isEqualTo(testValue);
    }

    /********************** Amount field ************************************/

    @Test
    void testInvoiceAmount() {
        invoiceBean.setAmount("123.45");
        var violations = validator.validateProperty(invoiceBean, "amount");
        assertThat(violations).isEmpty();
    }

    @Test
    void testEmptyInvoiceAmount() {
        invoiceBean.setAmount("");
        var violations = validator.validateProperty(invoiceBean, "amount");
        violations.forEach(violation -> System.out.println(violation.getMessage()));
        assertThat(violations).hasSize(2);
    }

    @Test
    void testNullInvoiceAmount() {
        invoiceBean.setAmount(null);
        var violations = validator.validateProperty(invoiceBean, "amount");
        violations.forEach(violation -> System.out.println(violation.getMessage()));
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Invoice Amount cannot be empty");
    }

    @Test
    void testInvalidInvoiceAmount() {
        invoiceBean.setAmount("123.456");
        var violations = validator.validateProperty(invoiceBean, "amount");
        violations.forEach(violation -> System.out.println(violation.getMessage()));
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Amount must be a number with up to two decimal places");
    }

    @Test
    void testInvoiceAmountGetterAndSetter() {
        String testValue = "123.45";
        invoiceBean.setAmount(testValue);
        assertThat(invoiceBean.getAmount()).isEqualTo(testValue);
    }

    @AfterAll
    void tearDown() {
        factory.close();
    }

//    @Test
//    void testSubmitInvoice() {
//        invoiceBean.submitInvoice();
//        var violations = validator.validate(invoiceBean);
//        assertThat(violations).isEmpty();
//    }
}