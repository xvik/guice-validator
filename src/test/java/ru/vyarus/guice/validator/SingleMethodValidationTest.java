package ru.vyarus.guice.validator;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runners.Parameterized;

import com.google.inject.Singleton;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.executable.ValidateOnExecution;
import java.util.Collection;

/**
 * Checks validation of only one method in service.
 *
 * @author Vyacheslav Rusakov
 * @since 24.06.2014
 */
public class SingleMethodValidationTest extends AbstractParameterizedTest<SingleMethodValidationTest.SampleService> {

    public SingleMethodValidationTest(String type, SampleService service) {
        super(type, service);
    }


    @Parameterized.Parameters(name = "{index}: {0}")
    public static Collection<Object[]> generateData() {
        return AbstractParameterizedTest.generateData(SampleService.class);
    }

    @Test
    public void testMethodValidation() throws Exception {
        if (isExplicit()) {
            service.noValidation(null);
        } else {
            try {
                service.noValidation(null);
                Assert.fail();
            } catch (ConstraintViolationException ex) {
            }
        }
    }

    @Test(expected = ConstraintViolationException.class)
    public void testMethodValidationEnabledFail() throws Exception {
        service.enabledValidation(null);
    }

    @Test
    public void testMethodValidationEnabled() throws Exception {
        service.enabledValidation(new Object());
    }

    @Singleton
    public static class SampleService {
        public void noValidation(@NotNull Object object) {
        }

        @ValidateOnExecution
        public void enabledValidation(@NotNull Object object) {
        }
    }
}
