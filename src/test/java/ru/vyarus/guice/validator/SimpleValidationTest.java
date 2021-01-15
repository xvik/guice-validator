package ru.vyarus.guice.validator;

import org.junit.Test;
import org.junit.runners.Parameterized;
import ru.vyarus.guice.validator.simple.SimpleBean;
import ru.vyarus.guice.validator.simple.SimpleService;

import jakarta.validation.ConstraintViolationException;
import java.util.Collection;

/**
 * Checks the simplest validation cases (params/return value)
 *
 * @author Vyacheslav Rusakov
 * @since 24.06.2014
 */
public class SimpleValidationTest extends AbstractParameterizedTest<SimpleService> {

    public SimpleValidationTest(String type, SimpleService service) {
        super(type, service);
    }

    @Parameterized.Parameters(name = "{index}: {0}")
    public static Collection<Object[]> generateData() {
        return AbstractParameterizedTest.generateData(SimpleService.class);
    }

    @Test(expected = ConstraintViolationException.class)
    public void SimpleBeanRequiredFail() throws Exception {
        service.beanRequired(null);
    }

    @Test
    public void SimpleBeanRequired() throws Exception {
        service.beanRequired(new SimpleBean());
    }

    @Test(expected = ConstraintViolationException.class)
    public void SimpleValidBeanRequiredFail() throws Exception {
        service.validBeanRequired(new SimpleBean());
    }

    @Test
    public void SimpleValidBeanRequired() throws Exception {
        service.validBeanRequired(new SimpleBean("user", 10));
    }

    @Test(expected = ConstraintViolationException.class)
    public void SimpleNotNullReturnFail() throws Exception {
        service.notNullReturn(null);
    }

    @Test
    public void SimpleNotNullReturn() throws Exception {
        service.notNullReturn(new SimpleBean());
    }

    @Test(expected = ConstraintViolationException.class)
    public void SimpleValidReturnFail() throws Exception {
        service.validReturn(new SimpleBean());
    }

    @Test
    public void SimpleValidReturn() throws Exception {
        service.notNullReturn(new SimpleBean("user", 10));
    }
}
