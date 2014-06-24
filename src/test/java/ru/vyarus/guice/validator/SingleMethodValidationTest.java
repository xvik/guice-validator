package ru.vyarus.guice.validator;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.inject.Singleton;
import javax.validation.ConstraintViolationException;
import javax.validation.constraints.NotNull;
import javax.validation.executable.ValidateOnExecution;

/**
 * Checks validation of only one method in service.
 *
 * @author Vyacheslav Rusakov
 * @since 24.06.2014
 */
public class SingleMethodValidationTest {

    private static SampleService sampleService;

    @BeforeClass
    public static void setUp() throws Exception {
        Injector injector = Guice.createInjector(new ValidationModule());
        sampleService = injector.getInstance(SampleService.class);
    }

    @Test
    public void testMethodValidation() throws Exception {
        sampleService.noValidation(null);
    }

    @Test(expected = ConstraintViolationException.class)
    public void testMethodValidationEnabledFail() throws Exception {
        sampleService.enabledValidation(null);
    }

    @Test
    public void testMethodValidationEnabled() throws Exception {
        sampleService.enabledValidation(new Object());
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
