package ru.vyarus.guice.validator;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.vyarus.guice.validator.customann.ExplicitMethod;
import ru.vyarus.guice.validator.customann.ToValidate;

import jakarta.validation.ConstraintViolationException;

/**
 * @author Vyacheslav Rusakov
 * @since 24.12.2019
 */
public class CustomExplicitAnnotationTest {

    static ExplicitMethod service;

    @BeforeClass
    public static void setUp() throws Exception {
        Injector injector = Guice.createInjector(new ValidationModule()
                .validateAnnotatedOnly(ToValidate.class));

        service = injector.getInstance(ExplicitMethod.class);
    }

    @Test
    public void testMatchedExclusion() throws Exception {
        service.doSmth(null);
        // error not happen, because method not annotated
    }

    @Test(expected = ConstraintViolationException.class)
    public void testExclusion() throws Exception {
        service.doSmth2(null);
        // method annotated
    }
}
