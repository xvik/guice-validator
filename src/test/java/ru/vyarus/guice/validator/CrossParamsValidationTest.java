package ru.vyarus.guice.validator;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.vyarus.guice.validator.crossparams.ComplexParamsService;

import javax.validation.ConstraintViolationException;

/**
 * Checks custom cross parameters validator.
 *
 * @author Vyacheslav Rusakov
 * @since 25.06.2014
 */
public class CrossParamsValidationTest {
    private static ComplexParamsService complexParamsService;

    @BeforeClass
    public static void setUp() throws Exception {
        Injector injector = Guice.createInjector(new ValidationModule());
        complexParamsService = injector.getInstance(ComplexParamsService.class);
    }

    @Test(expected = ConstraintViolationException.class)
    public void testValidationFail() throws Exception {
        complexParamsService.action(2, 12);
    }

    @Test
    public void testValidation() throws Exception {
        complexParamsService.action(1, 12);
    }
}
