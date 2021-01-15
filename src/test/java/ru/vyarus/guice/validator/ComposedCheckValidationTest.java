package ru.vyarus.guice.validator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ru.vyarus.guice.validator.compositeannotation.ComposedCheckService;

import jakarta.validation.ConstraintViolationException;
import java.util.Collection;

/**
 * @author Vyacheslav Rusakov
 * @since 25.06.2014
 */
@RunWith(Parameterized.class)
public class ComposedCheckValidationTest extends AbstractParameterizedTest<ComposedCheckService> {

    public ComposedCheckValidationTest(String type, ComposedCheckService service) {
        super(type, service);
    }

    @Parameterized.Parameters(name = "{index}: {0}")
    public static Collection<Object[]> generateData() {
        return AbstractParameterizedTest.generateData(ComposedCheckService.class);
    }

    @Test(expected = ConstraintViolationException.class)
    public void testValidationFail() throws Exception {
        service.checkParam("f");
    }

    @Test
    public void testValidation() {
        service.checkParam("valid string");
    }

    // todo can't make composed annotation work for return value
//    @Test(expected = ConstraintViolationException.class)
//    public void testReturn() {
//        service.checkReturn("valid string");
//    }
}
