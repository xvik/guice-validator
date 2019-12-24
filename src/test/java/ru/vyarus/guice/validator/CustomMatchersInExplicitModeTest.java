package ru.vyarus.guice.validator;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.matcher.Matchers;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.vyarus.guice.validator.customann.SuppressedExplicitClass;
import ru.vyarus.guice.validator.customann.SuppressedExplicitMethod;
import ru.vyarus.guice.validator.customann.ToValidate;
import ru.vyarus.guice.validator.matcher.SuppressValidation;

import javax.validation.ConstraintViolationException;

/**
 * @author Vyacheslav Rusakov
 * @since 24.12.2019
 */
public class CustomMatchersInExplicitModeTest {

    static SuppressedExplicitMethod method;
    static SuppressedExplicitClass type;

    @BeforeClass
    public static void setUp() throws Exception {
        Injector injector = Guice.createInjector(new ValidationModule()
                .validateAnnotatedOnly(ToValidate.class)
                .targetClasses(Matchers.not(Matchers.annotatedWith(SuppressValidation.class)))
                .targetMethods(Matchers.not(Matchers.annotatedWith(SuppressValidation.class))));

        method = injector.getInstance(SuppressedExplicitMethod.class);
        type = injector.getInstance(SuppressedExplicitClass.class);
    }

    @Test(expected = ConstraintViolationException.class)
    public void testNoSuppression() throws Exception {
        method.doSmth(null);
        // error
    }

    @Test
    public void testMethodSuppression() throws Exception {
        method.doSmth2(null);
        // method suppressed
    }

    @Test
    public void testClassSuppression() throws Exception {
        type.doSmth(null);
        // type suppressed
    }
}
