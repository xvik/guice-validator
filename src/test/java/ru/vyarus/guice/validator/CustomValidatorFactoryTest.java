package ru.vyarus.guice.validator;

import com.google.inject.Guice;
import org.junit.Test;
import ru.vyarus.guice.validator.customtype.ComplexBean;
import ru.vyarus.guice.validator.customtype.CustomService;
import ru.vyarus.guice.validator.simple.SimpleBean;
import ru.vyarus.guice.validator.simple.SimpleService;

import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;

/**
 * @author Vyacheslav Rusakov
 * @since 19.12.2014
 */
public class CustomValidatorFactoryTest {

    @Test(expected = ConstraintViolationException.class)
    public void testExplicitCustomFactory() throws Exception {
        Guice.createInjector(new ValidationModule(Validation.buildDefaultValidatorFactory()).validateAnnotatedOnly())
                .getInstance(SimpleService.class)
                .validReturn(new SimpleBean());
    }

    @Test(expected = ConstraintViolationException.class)
    public void testImplicitCustomFactory() throws Exception {
        Guice.createInjector(new ValidationModule(Validation.buildDefaultValidatorFactory()))
                .getInstance(SimpleService.class)
                .validReturn(new SimpleBean());
    }

    @Test
    public void testConstraintCleanup() throws Exception {
        final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Guice.createInjector(new ValidationModule(factory).validateAnnotatedOnly())
                .getInstance(CustomService.class)
                .doAction(new ComplexBean("perfect", 12));
        factory.close();
    }
}
