package ru.vyarus.guice.validator;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.vyarus.guice.validator.customtype.ComplexBean;
import ru.vyarus.guice.validator.customtype.CustomService;

import javax.validation.ConstraintViolationException;

/**
 * Check custom validator with injection.
 *
 * @author Vyacheslav Rusakov
 * @since 24.06.2014
 */
public class CustomTypeValidationTest {
    private static CustomService customService;

    @BeforeClass
    public static void setUp() throws Exception {
        Injector injector = Guice.createInjector(new ValidationModule());
        customService = injector.getInstance(CustomService.class);
    }

    @Test(expected = ConstraintViolationException.class)
    public void testValidationFail() throws Exception {
        customService.doAction(new ComplexBean("soso", 12));
    }

    @Test
    public void testValidation() throws Exception {
        customService.doAction(new ComplexBean("perfect", 12));
    }
}
