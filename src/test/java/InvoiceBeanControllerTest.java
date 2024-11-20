import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.acme.view.InvoiceBean;
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
        assertThat(violations).isEmpty();
    }

    @Test
    void testEmptyInvoiceNumber() {
        invoiceBeanController.setInvoiceNumber("");
        var violations = validator.validateProperty(invoiceBeanController, "invoiceNumber");
        violations.forEach(violation ->
                System.out.println(" ===================  " + violation.getMessage()));
        assertThat(violations).hasSize(2);
    }

    @Test
    void testNullInvoiceNumber() {
        invoiceBeanController.setInvoiceNumber(null);
        var violations = validator.validateProperty(invoiceBeanController, "invoiceNumber");
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Invoice Number cannot not be empty");
    }

    @Test
    void testNonDigitInvoiceNumber() {
        invoiceBeanController.setInvoiceNumber("12a3");
        var violations = validator.validateProperty(invoiceBeanController, "invoiceNumber");
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Invoice number must contain only digits");
    }

    @Test
    void testInvoiceNumberGetterAndSetter() {
        String testValue = "456";
        invoiceBeanController.setInvoiceNumber(testValue);
        assertThat(invoiceBeanController.getInvoiceNumber()).isEqualTo(testValue);
    }

    @Test
    void testGetInvoiceNumberAsInteger() {
        invoiceBeanController.setInvoiceNumber("789");
        assertThat(invoiceBeanController.getInvoiceNumberAsInteger()).isEqualTo(789);
    }

    /********************** Invoice date field ************************************/

    @Test
    void testValidInvoiceDate() {
        invoiceBeanController.setInvoiceDate(LocalDate.of(2023, 8, 1));
        var violations = validator.validateProperty(invoiceBeanController, "invoiceDate");
        assertThat(violations).isEmpty();
    }

    @Test
    void testEmptyInvoiceDate() {
        var violations = validator.validateProperty(invoiceBeanController, "invoiceDate");
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Invoice Date cannot be empty");
        assertThatThrownBy(() -> LocalDate.parse(""))
            .isInstanceOf(DateTimeParseException.class);
    }

    @Test
    void testNullInvoiceDate() {
        invoiceBeanController.setInvoiceDate(null);
        var violations = validator.validateProperty(invoiceBeanController, "invoiceDate");
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Invoice Date cannot be empty");
    }

    @Test
    void testInvoiceDateGetterAndSetter() {
        LocalDate testDate = LocalDate.of(2023, 8, 1);
        invoiceBeanController.setInvoiceDate(testDate);
        assertThat(invoiceBeanController.getInvoiceDate()).isEqualTo(testDate);
    }

    /********************** Customer name field ************************************/

    @Test
    void testValidCustomerName() {
        invoiceBeanController.setCustomerName("John Doe");
        var violations = validator.validateProperty(invoiceBeanController, "customerName");
        assertThat(violations).isEmpty();
    }

    @Test
    void testEmptyCustomerName() {
        invoiceBeanController.setCustomerName("");
        var violations = validator.validateProperty(invoiceBeanController, "customerName");
        violations.forEach(violation -> System.out.println(violation.getMessage()));
        assertThat(violations).hasSize(2);
    }

    @Test
    void testNullCustomerName() {
        invoiceBeanController.setCustomerName(null);
        var violations = validator.validateProperty(invoiceBeanController, "customerName");
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Customer name cannot be empty");
    }

    @Test
    void testInvalidCustomerName() {
        invoiceBeanController.setCustomerName("John Doe123");
        var violations = validator.validateProperty(invoiceBeanController, "customerName");
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Customer name must contain only letters and spaces");
    }

    @Test
    void testCustomerNameGetterAndSetter() {
        String testValue = "Jane Doe";
        invoiceBeanController.setCustomerName(testValue);
        assertThat(invoiceBeanController.getCustomerName()).isEqualTo(testValue);
    }

    /********************** Amount field ************************************/

    @Test
    void testInvoiceAmount() {
        invoiceBeanController.setAmount("123.45");
        var violations = validator.validateProperty(invoiceBeanController, "amount");
        assertThat(violations).isEmpty();
    }

    @Test
    void testEmptyInvoiceAmount() {
        invoiceBeanController.setAmount("");
        var violations = validator.validateProperty(invoiceBeanController, "amount");
        violations.forEach(violation -> System.out.println(violation.getMessage()));
        assertThat(violations).hasSize(2);
    }

    @Test
    void testNullInvoiceAmount() {
        invoiceBeanController.setAmount(null);
        var violations = validator.validateProperty(invoiceBeanController, "amount");
        violations.forEach(violation -> System.out.println(violation.getMessage()));
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Invoice Amount cannot be empty");
    }

    @Test
    void testInvalidInvoiceAmount() {
        invoiceBeanController.setAmount("123.456");
        var violations = validator.validateProperty(invoiceBeanController, "amount");
        violations.forEach(violation -> System.out.println(violation.getMessage()));
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Amount must be a number with up to two decimal places");
    }

    @Test
    void testInvoiceAmountGetterAndSetter() {
        String testValue = "123.45";
        invoiceBeanController.setAmount(testValue);
        assertThat(invoiceBeanController.getAmount()).isEqualTo(testValue);
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