package ru.vyarus.guice.validator;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.vyarus.guice.validator.simple.SimpleBean;
import ru.vyarus.guice.validator.simple.SimpleService;

import javax.validation.ConstraintViolationException;

/**
 * Checks the simplest validation cases (params/return value)
 *
 * @author Vyacheslav Rusakov
 * @since 24.06.2014
 */
public class SimpleValidationTest {

    private static SimpleService simpleService;

    @BeforeClass
    public static void setUp() throws Exception {
        Injector injector = Guice.createInjector(new ValidationModule());
        simpleService = injector.getInstance(SimpleService.class);
    }

    @Test(expected = ConstraintViolationException.class)
    public void SimpleBeanRequiredFail() throws Exception {
        simpleService.beanRequired(null);
    }

    @Test
    public void SimpleBeanRequired() throws Exception {
        simpleService.beanRequired(new SimpleBean());
    }

    @Test(expected = ConstraintViolationException.class)
    public void SimpleValidBeanRequiredFail() throws Exception {
        simpleService.validBeanRequired(new SimpleBean());
    }

    @Test
    public void SimpleValidBeanRequired() throws Exception {
        simpleService.validBeanRequired(new SimpleBean("user", 10));
    }

    @Test(expected = ConstraintViolationException.class)
    public void SimpleNotNullReturnFail() throws Exception {
        simpleService.notNullReturn(null);
    }

    @Test
    public void SimpleNotNullReturn() throws Exception {
        simpleService.notNullReturn(new SimpleBean());
    }

    @Test(expected = ConstraintViolationException.class)
    public void SimpleValidReturnFail() throws Exception {
        simpleService.validReturn(new SimpleBean());
    }

    @Test
    public void SimpleValidReturn() throws Exception {
        simpleService.notNullReturn(new SimpleBean("user", 10));
    }
}
