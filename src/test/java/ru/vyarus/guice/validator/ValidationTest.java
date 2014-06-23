package ru.vyarus.guice.validator;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.vyarus.guice.validator.module.TestBean;
import ru.vyarus.guice.validator.module.TestService;

import javax.validation.ConstraintViolationException;

/**
 * @author Vyacheslav Rusakov (vyarus@gmail.com)
 * @since 24.06.2014.
 */
public class ValidationTest {

    private static Injector injector;
    private static TestService testService;

    @BeforeClass
    public static void setUp() throws Exception {
        injector = Guice.createInjector(new ValidationModule());
        testService = injector.getInstance(TestService.class);
    }

    @Test(expected = ConstraintViolationException.class)
    public void testBeanRequiredFail() throws Exception {
        testService.beanRequired(null);
    }

    @Test
    public void testBeanRequired() throws Exception {
        testService.beanRequired(new TestBean());
    }

    @Test(expected = ConstraintViolationException.class)
    public void testValidBeanRequiredFail() throws Exception {
        testService.validBeanRequired(new TestBean());
    }

    @Test
    public void testValidBeanRequired() throws Exception {
        testService.validBeanRequired(new TestBean("user", 10));
    }

    @Test(expected = ConstraintViolationException.class)
    public void testNotNullReturnFail() throws Exception {
        testService.notNullReturn(null);
    }

    @Test
    public void testNotNullReturn() throws Exception {
        testService.notNullReturn(new TestBean());
    }

    @Test(expected = ConstraintViolationException.class)
    public void testValidReturnFail() throws Exception {
        testService.validReturn(new TestBean());
    }

    @Test
    public void testValidReturn() throws Exception {
        testService.notNullReturn(new TestBean("user", 10));
    }
}
