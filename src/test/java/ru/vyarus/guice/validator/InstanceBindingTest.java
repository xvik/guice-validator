package ru.vyarus.guice.validator;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Module;
import org.junit.Test;
import org.junit.runners.Parameterized;
import ru.vyarus.guice.validator.simple.SimpleService;

import javax.validation.ConstraintViolationException;
import java.util.Arrays;
import java.util.Collection;

/**
 * Shows that validation will not work on beans bind as instances.
 *
 * @author Vyacheslav Rusakov
 * @since 20.12.2014
 */
public class InstanceBindingTest extends AbstractParameterizedTest<SimpleService>{

    public InstanceBindingTest(String type, SimpleService service) {
        super(type, service);
    }

    @Parameterized.Parameters(name = "{index}: {0}")
    public static Collection<Object[]> generateData() {
        final Class type = SimpleService.class;
        final Module module = new AbstractModule() {
            @Override
            protected void configure() {
                bind(SimpleService.class).toInstance(new SimpleService());
            }
        };
        return Arrays.asList(new Object[][]{
                {EXPLICIT, Guice.createInjector(new ValidationModule(), module).getInstance(type)},
                {IMPLICIT, Guice.createInjector(new ImplicitValidationModule(), module).getInstance(type)}
        });
    }

    @Test
    public void SimpleBeanRequiredFail() throws Exception {
        service.beanRequired(null);
        // validation should fail, but aop can't be applied to instances
    }

}
