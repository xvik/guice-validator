package ru.vyarus.guice.validator;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.vyarus.guice.validator.compositeannotation.ComposedCheckService;

import javax.validation.ConstraintViolationException;

/**
 * @author Vyacheslav Rusakov
 * @since 25.06.2014
 */
public class ComposedCheckValidationTest {
    private static ComposedCheckService composedCheckService;

    @BeforeClass
    public static void setUp() throws Exception {
        Injector injector = Guice.createInjector(new ValidationModule());
        composedCheckService = injector.getInstance(ComposedCheckService.class);
    }

    @Test(expected = ConstraintViolationException.class)
    public void testValidationFail() throws Exception {
        composedCheckService.checkParam("f");
    }

    @Test
    public void testValidation() {
        composedCheckService.checkParam("valid string");
    }

    // todo can't make composed annotation work for return value
//    @Test(expected = ConstraintViolationException.class)
//    public void testReturn() {
//        composedCheckService.checkReturn("valid string");
//    }
}
