package ru.vyarus.guice.validator;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.executable.ExecutableValidator;
import javax.validation.executable.ValidateOnExecution;

/**
 * Adds {@code Validator} bean to guice context (to make it available for manual validations).
 * Method validations will activate by annotation {@code ValidateOnExecution} on class or method.
 *
 * @author Vyacheslav Rusakov
 * @since 24.06.2014
 */
public class ValidationModule extends AbstractModule {

    private ValidatorFactory factory;

    /**
     * Creates module using default validator factory.
     */
    public ValidationModule() {
        this(Validation.buildDefaultValidatorFactory());
    }

    /**
     * Create module using custom validator factory.
     * Useful if other (non guice) parts use validation too and don't want to know about guice.
     *
     * @param factory base factory
     */
    public ValidationModule(ValidatorFactory factory) {
        this.factory = factory;
    }

    @Override
    protected void configure() {
        GuiceConstraintValidatorFactory constraintValidatorFactory = new GuiceConstraintValidatorFactory();
        requestInjection(constraintValidatorFactory);

        /* Overriding just constraints factory to allow them use guice injections */
        Validator validator = factory.usingContext()
                .constraintValidatorFactory(constraintValidatorFactory)
                .getValidator();

        bind(Validator.class).toInstance(validator);
        bind(ExecutableValidator.class).toInstance(validator.forExecutables());

        GuiceMethodValidator interceptor = new GuiceMethodValidator();
        requestInjection(interceptor);

        /* Using explicit annotation matching because annotations may be used just for compile time checks
        (see hibernate validator apt lib). Moreover it makes "automatic" validation more explicit.
        Such annotation usage is contradict with its javadoc, but annotation name is ideal for use case, so why introduce new one).*/
        bindInterceptor(Matchers.any(), Matchers.annotatedWith(ValidateOnExecution.class), interceptor);
        bindInterceptor(Matchers.annotatedWith(ValidateOnExecution.class), Matchers.any(), interceptor);
    }
}
