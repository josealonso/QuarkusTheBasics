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

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class InvoiceBeanControllerTest {

    @Inject
    private Validator validator;

    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();

    //    @InjectMock
    private InvoiceBean invoiceBeanController;

    @BeforeEach
    void setUp() {
        validator = factory.getValidator();
        invoiceBeanController = new InvoiceBean();
    }

    /*********************** Invoice number field ************************************/

    @Test
    void testValidInvoiceNumber() {
        invoiceBeanController.setInvoiceNumber("12");
        var violations = validator.validateProperty(invoiceBeanController, "invoiceNumber");
        violations.forEach(violation ->
                System.out.println(" ===================  " + violation.getMessage()));
        assertTrue(violations.isEmpty(), "Expected no violations");
    }

    @Test
    void testEmptyInvoiceNumber() {
        invoiceBeanController.setInvoiceNumber("");
        var violations = validator.validateProperty(invoiceBeanController, "invoiceNumber");
        violations.forEach(violation ->
                System.out.println(" ===================  " + violation.getMessage()));
        assertEquals(2, violations.size());
//        assertEquals("Invoice Number cannot not be empty", violations.iterator().next().getMessage());
    }

    @Test
    void testNullInvoiceNumber() {
        invoiceBeanController.setInvoiceNumber(null);
        var violations = validator.validateProperty(invoiceBeanController, "invoiceNumber");
        assertEquals(1, violations.size());
        assertEquals("Invoice Number cannot not be empty", violations.iterator().next().getMessage());
    }

    @Test
    void testNonDigitInvoiceNumber() {
        invoiceBeanController.setInvoiceNumber("12a3");
        var violations = validator.validateProperty(invoiceBeanController, "invoiceNumber");
        assertEquals(1, violations.size());
        assertEquals("Invoice number must contain only digits", violations.iterator().next().getMessage());
    }

    @Test
    void testInvoiceNumberGetterAndSetter() {
        String testValue = "456";
        invoiceBeanController.setInvoiceNumber(testValue);
        assertEquals(testValue, invoiceBeanController.getInvoiceNumber());
    }

    @Test
    void testGetInvoiceNumberAsInteger() {
        invoiceBeanController.setInvoiceNumber("789");
        assertEquals(Integer.valueOf(789), invoiceBeanController.getInvoiceNumberAsInteger());
    }

    /********************** Invoice date field ************************************/

    @Test
    void testValidInvoiceDate() {
        invoiceBeanController.setInvoiceDate(LocalDate.of(2023, 8, 1));
        var violations = validator.validateProperty(invoiceBeanController, "invoiceDate");
        assertTrue(violations.isEmpty());
    }

    @Test
    void testEmptyInvoiceDate() {
        var violations = validator.validateProperty(invoiceBeanController, "invoiceDate");
        assertEquals(1, violations.size());
        assertThrows(DateTimeParseException.class, () -> LocalDate.parse(""));
        assertEquals("Invoice Date cannot be empty", violations.iterator().next().getMessage());
    }

    @Test
    void testNullInvoiceDate() {
        invoiceBeanController.setInvoiceDate(null);
        var violations = validator.validateProperty(invoiceBeanController, "invoiceDate");
        assertEquals(1, violations.size());
        assertEquals("Invoice Date cannot be empty", violations.iterator().next().getMessage());
    }

    @Test
    void testInvoiceDateGetterAndSetter() {
        LocalDate testValue = LocalDate.of(2023, 8, 1);
        invoiceBeanController.setInvoiceDate(testValue);
        assertEquals(testValue, invoiceBeanController.getInvoiceDate());
    }

    /********************** Customer name field ************************************/

    @Test
    void testValidCustomerName() {
        invoiceBeanController.setCustomerName("John Doe");
        var violations = validator.validateProperty(invoiceBeanController, "customerName");
        assertTrue(violations.isEmpty());
    }

    @Test
    void testEmptyCustomerName() {
        invoiceBeanController.setCustomerName("");
        var violations = validator.validateProperty(invoiceBeanController, "customerName");
        violations.forEach(violation -> System.out.println(violation.getMessage()));
        assertEquals(2, violations.size());
//        assertEquals("Customer name cannot be empty", violations.iterator().next().getMessage());
//        assertEquals("Customer name must contain only letters and spaces", violations.iterator().next().getMessage());
    }

    @Test
    void testNullCustomerName() {
        invoiceBeanController.setCustomerName(null);
        var violations = validator.validateProperty(invoiceBeanController, "customerName");
        assertEquals(1, violations.size());
        assertEquals("Customer name cannot be empty", violations.iterator().next().getMessage());
    }

    @Test
    void testInvalidCustomerName() {
        invoiceBeanController.setCustomerName("John Doe123");
        var violations = validator.validateProperty(invoiceBeanController, "customerName");
        assertEquals(1, violations.size());
        assertEquals("Customer name must contain only letters and spaces", violations.iterator().next().getMessage());
    }

    @Test
    void testCustomerNameGetterAndSetter() {
        String testValue = "Jane Doe";
        invoiceBeanController.setCustomerName(testValue);
        assertEquals(testValue, invoiceBeanController.getCustomerName());
    }

    /********************** Amount field ************************************/

    @Test
    void testInvoiceAmount() {
        invoiceBeanController.setAmount("123.45");
        var violations = validator.validateProperty(invoiceBeanController, "amount");
        assertTrue(violations.isEmpty());
    }

    @Test
    void testEmptyInvoiceAmount() {
        invoiceBeanController.setAmount("");
        var violations = validator.validateProperty(invoiceBeanController, "amount");
        violations.forEach(violation -> System.out.println(violation.getMessage()));
        assertEquals(2, violations.size());
    }

    @Test
    void testNullInvoiceAmount() {
        invoiceBeanController.setAmount(null);
        var violations = validator.validateProperty(invoiceBeanController, "amount");
        violations.forEach(violation -> System.out.println(violation.getMessage()));
        assertEquals(1, violations.size());
        assertEquals("Invoice Amount cannot be empty", violations.iterator().next().getMessage());
    }

    @Test
    void testInvalidInvoiceAmount() {
        invoiceBeanController.setAmount("123.456");
        var violations = validator.validateProperty(invoiceBeanController, "amount");
        violations.forEach(violation -> System.out.println(violation.getMessage()));
        assertEquals(1, violations.size());
        assertEquals("Amount must be a number with up to two decimal places",
                violations.iterator().next().getMessage());
    }

    @Test
    void testInvoiceAmountGetterAndSetter() {
        String testValue = "123.45";
        invoiceBeanController.setAmount(testValue);
        assertEquals(testValue, invoiceBeanController.getAmount());
    }

    @AfterAll
    void tearDown() {
        factory.close();
    }

//    @Test
//    void testSubmitInvoice() {
//        invoiceBean.submitInvoice();
//        var violations = validator.validate(invoiceBean);
//        assertTrue(violations.isEmpty());
//    }



























}