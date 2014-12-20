package ru.vyarus.guice.validator;

import com.google.inject.matcher.Matchers;
import ru.vyarus.guice.validator.aop.ValidationMethodInterceptor;

import javax.validation.ValidatorFactory;
import javax.validation.executable.ValidateOnExecution;

/**
 * Explicit validation activation.
 * Method validations will activate by annotation {@code ValidateOnExecution} on class or method.
 *
 * @author Vyacheslav Rusakov
 * @see ru.vyarus.guice.validator.ImplicitValidationModule for implicit validation (without additinoal annotation)
 * @since 24.06.2014
 */
public class ValidationModule extends AbstractValidationModule {


    /**
     * Creates module using default validator factory.
     */
    public ValidationModule() {
        // default validation factory
    }

    /**
     * Create module using custom validator factory.
     * Useful if other (non guice) parts use validation too and don't want to know about guice.
     *
     * @param factory base factory
     */
    public ValidationModule(final ValidatorFactory factory) {
        super(factory);
    }

    @Override
    protected void configureAop(final ValidationMethodInterceptor interceptor) {
        /* Using explicit annotation matching because annotations may be used just for compile time checks
        (see hibernate validator apt lib). Moreover it makes "automatic" validation more explicit.
        Such annotation usage is contradict with its javadoc,
        but annotation name is ideal for use case, so why introduce new one).*/
        bindInterceptor(Matchers.any(), Matchers.annotatedWith(ValidateOnExecution.class), interceptor);
        bindInterceptor(Matchers.annotatedWith(ValidateOnExecution.class), Matchers.any(), interceptor);
    }
}
