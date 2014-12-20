package ru.vyarus.guice.validator;

import org.junit.Test;
import org.junit.runners.Parameterized;
import ru.vyarus.guice.validator.crossparams.ComplexParamsService;

import javax.validation.ConstraintViolationException;
import java.util.Collection;

/**
 * Checks custom cross parameters validator.
 *
 * @author Vyacheslav Rusakov
 * @since 25.06.2014
 */
public class CrossParamsValidationTest extends AbstractParameterizedTest<ComplexParamsService> {

    public CrossParamsValidationTest(String type, ComplexParamsService service) {
        super(type, service);
    }

    @Parameterized.Parameters(name = "{index}: {0}")
    public static Collection<Object[]> generateData() {
        return AbstractParameterizedTest.generateData(ComplexParamsService.class);
    }

    @Test(expected = ConstraintViolationException.class)
    public void testValidationFail() throws Exception {
        service.action(2, 12);
    }

    @Test
    public void testValidation() throws Exception {
        service.action(1, 12);
    }
}
