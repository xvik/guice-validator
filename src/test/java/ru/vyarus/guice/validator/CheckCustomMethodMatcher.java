package ru.vyarus.guice.validator;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.matcher.Matchers;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.vyarus.guice.validator.matcher.ExceptionalMethod;
import ru.vyarus.guice.validator.matcher.SuppressValidation;

import jakarta.validation.ConstraintViolationException;

/**
 * @author Vyacheslav Rusakov
 * @since 24.12.2019
 */
public class CheckCustomMethodMatcher {

    static ExceptionalMethod exceptional;

    @BeforeClass
    public static void setUp() throws Exception {
        Injector injector = Guice.createInjector(new ValidationModule()
                .targetMethods(Matchers.not(Matchers.annotatedWith(SuppressValidation.class))));

        exceptional = injector.getInstance(ExceptionalMethod.class);
    }

    @Test
    public void testMatchedExclusion() throws Exception {
        exceptional.doSmth(null);
        // error not happen, because matcher exclude type from aop processing
    }

    @Test(expected = ConstraintViolationException.class)
    public void testExclusion() throws Exception {
        exceptional.doSmth2(null);
        // method not suppressed
    }
}
