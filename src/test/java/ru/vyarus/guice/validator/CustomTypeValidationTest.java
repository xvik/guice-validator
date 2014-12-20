package ru.vyarus.guice.validator;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runners.Parameterized;
import ru.vyarus.guice.validator.customtype.ComplexBean;
import ru.vyarus.guice.validator.customtype.CustomService;

import javax.validation.ConstraintViolationException;
import java.util.Collection;

/**
 * Check custom validator with injection.
 *
 * @author Vyacheslav Rusakov
 * @since 24.06.2014
 */
public class CustomTypeValidationTest extends AbstractParameterizedTest<CustomService> {

    public CustomTypeValidationTest(String type, CustomService service) {
        super(type, service);
    }

    @Parameterized.Parameters(name = "{index}: {0}")
    public static Collection<Object[]> generateData() {
        return AbstractParameterizedTest.generateData(CustomService.class);
    }

    @Test(expected = ConstraintViolationException.class)
    public void testValidationFail() throws Exception {
        service.doAction(new ComplexBean("soso", 12));
    }

    @Test
    public void testValidation() throws Exception {
        service.doAction(new ComplexBean("perfect", 12));
    }
}
