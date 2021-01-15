package ru.vyarus.guice.validator;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.matcher.Matchers;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.vyarus.guice.validator.matcher.ExceptionalService;
import ru.vyarus.guice.validator.matcher.MatchedService;
import ru.vyarus.guice.validator.matcher.SuppressValidation;

import jakarta.validation.ConstraintViolationException;

/**
 * @author Vyacheslav Rusakov
 * @since 20.12.2014
 */
public class CheckCustomMatcher {

    static MatchedService matchedService;
    static ExceptionalService exceptionalService;

    @BeforeClass
    public static void setUp() throws Exception {
        Injector injector = Guice.createInjector(new ValidationModule()
                .targetClasses(Matchers.not(Matchers.annotatedWith(SuppressValidation.class))));

        matchedService = injector.getInstance(MatchedService.class);
        exceptionalService = injector.getInstance(ExceptionalService.class);
    }

    @Test(expected = ConstraintViolationException.class)
    public void testCustomMatcher() throws Exception {
        matchedService.doSmth(null);
    }

    @Test
    public void testMatchedExclusion() throws Exception {
        exceptionalService.doSmth(null);
        // error not happen, because matcher exclude type from aop processing
    }
}
