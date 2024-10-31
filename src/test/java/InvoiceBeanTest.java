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
class InvoiceBeanTest {

    @Inject
    private Validator validator;

    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();

    //    @InjectMock
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
        assertTrue(violations.isEmpty(), "Expected no violations");
    }

    @Test
    void testEmptyInvoiceNumber() {
        invoiceBean.setInvoiceNumber("");
        var violations = validator.validateProperty(invoiceBean, "invoiceNumber");
        violations.forEach(violation ->
                System.out.println(" ===================  " + violation.getMessage()));
        assertEquals(2, violations.size());
//        assertEquals("Invoice Number cannot not be empty", violations.iterator().next().getMessage());
    }

    @Test
    void testNullInvoiceNumber() {
        invoiceBean.setInvoiceNumber(null);
        var violations = validator.validateProperty(invoiceBean, "invoiceNumber");
        assertEquals(1, violations.size());
        assertEquals("Invoice Number cannot not be empty", violations.iterator().next().getMessage());
    }

    @Test
    void testNonDigitInvoiceNumber() {
        invoiceBean.setInvoiceNumber("12a3");
        var violations = validator.validateProperty(invoiceBean, "invoiceNumber");
        assertEquals(1, violations.size());
        assertEquals("Invoice number must contain only digits", violations.iterator().next().getMessage());
    }

    @Test
    void testInvoiceNumberGetterAndSetter() {
        String testValue = "456";
        invoiceBean.setInvoiceNumber(testValue);
        assertEquals(testValue, invoiceBean.getInvoiceNumber());
    }

    @Test
    void testGetInvoiceNumberAsInteger() {
        invoiceBean.setInvoiceNumber("789");
        assertEquals(Integer.valueOf(789), invoiceBean.getInvoiceNumberAsInteger());
    }

    /********************** Invoice date field ************************************/

    @Test
    void testValidInvoiceDate() {
        invoiceBean.setInvoiceDate(LocalDate.of(2023, 8, 1));
        var violations = validator.validateProperty(invoiceBean, "invoiceDate");
        assertTrue(violations.isEmpty());
    }

    @Test
    void testEmptyInvoiceDate() {
        var violations = validator.validateProperty(invoiceBean, "invoiceDate");
        assertEquals(1, violations.size());
        assertThrows(DateTimeParseException.class, () -> LocalDate.parse(""));
        assertEquals("Invoice Date cannot be empty", violations.iterator().next().getMessage());
    }

    @Test
    void testNullInvoiceDate() {
        invoiceBean.setInvoiceDate(null);
        var violations = validator.validateProperty(invoiceBean, "invoiceDate");
        assertEquals(1, violations.size());
        assertEquals("Invoice Date cannot be empty", violations.iterator().next().getMessage());
    }

    @Test
    void testInvoiceDateGetterAndSetter() {
        LocalDate testValue = LocalDate.of(2023, 8, 1);
        invoiceBean.setInvoiceDate(testValue);
        assertEquals(testValue, invoiceBean.getInvoiceDate());
    }

    /********************** Customer name field ************************************/

    @Test
    void testValidCustomerName() {
        invoiceBean.setCustomerName("John Doe");
        var violations = validator.validateProperty(invoiceBean, "customerName");
        assertTrue(violations.isEmpty());
    }

    @Test
    void testEmptyCustomerName() {
        invoiceBean.setCustomerName("");
        var violations = validator.validateProperty(invoiceBean, "customerName");
        violations.forEach(violation -> System.out.println(violation.getMessage()));
        assertEquals(2, violations.size());
//        assertEquals("Customer name cannot be empty", violations.iterator().next().getMessage());
//        assertEquals("Customer name must contain only letters and spaces", violations.iterator().next().getMessage());
    }

    @Test
    void testNullCustomerName() {
        invoiceBean.setCustomerName(null);
        var violations = validator.validateProperty(invoiceBean, "customerName");
        assertEquals(1, violations.size());
        assertEquals("Customer name cannot be empty", violations.iterator().next().getMessage());
    }

    @Test
    void testInvalidCustomerName() {
        invoiceBean.setCustomerName("John Doe123");
        var violations = validator.validateProperty(invoiceBean, "customerName");
        assertEquals(1, violations.size());
        assertEquals("Customer name must contain only letters and spaces", violations.iterator().next().getMessage());
    }

    @Test
    void testCustomerNameGetterAndSetter() {
        String testValue = "Jane Doe";
        invoiceBean.setCustomerName(testValue);
        assertEquals(testValue, invoiceBean.getCustomerName());
    }

    /********************** Amount field ************************************/

    @Test
    void testInvoiceAmount() {
        invoiceBean.setAmount("123.45");
        var violations = validator.validateProperty(invoiceBean, "amount");
        assertTrue(violations.isEmpty());
    }

    @Test
    void testEmptyInvoiceAmount() {
        invoiceBean.setAmount("");
        var violations = validator.validateProperty(invoiceBean, "amount");
        violations.forEach(violation -> System.out.println(violation.getMessage()));
        assertEquals(2, violations.size());
    }

    @Test
    void testNullInvoiceAmount() {
        invoiceBean.setAmount(null);
        var violations = validator.validateProperty(invoiceBean, "amount");
        violations.forEach(violation -> System.out.println(violation.getMessage()));
        assertEquals(1, violations.size());
        assertEquals("Invoice Amount cannot be empty", violations.iterator().next().getMessage());
    }

    @Test
    void testInvalidInvoiceAmount() {
        invoiceBean.setAmount("123.456");
        var violations = validator.validateProperty(invoiceBean, "amount");
        violations.forEach(violation -> System.out.println(violation.getMessage()));
        assertEquals(1, violations.size());
        assertEquals("Amount must be a number with up to two decimal places",
                violations.iterator().next().getMessage());
    }

    @Test
    void testInvoiceAmountGetterAndSetter() {
        String testValue = "123.45";
        invoiceBean.setAmount(testValue);
        assertEquals(testValue, invoiceBean.getAmount());
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