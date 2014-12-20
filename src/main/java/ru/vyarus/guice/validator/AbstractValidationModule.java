package ru.vyarus.guice.validator;

import com.google.inject.AbstractModule;
import ru.vyarus.guice.validator.aop.ValidationMethodInterceptor;
import ru.vyarus.guice.validator.constraint.GuiceConstraintValidatorFactory;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.executable.ExecutableValidator;

/**
 * Base for validation modules. Binds {@link javax.validation.Validator},
 * {@link javax.validation.executable.ExecutableValidator} and
 * {@link javax.validation.ValidatorFactory} instances to context.
 * Specific module implementation provides aop bindings.
 *
 * @author Vyacheslav Rusakov
 * @since 19.12.2014
 */
public abstract class AbstractValidationModule extends AbstractModule {

    private ValidatorFactory factory;

    /**
     * Creates module using default validator factory.
     */
    public AbstractValidationModule() {
        this(Validation.buildDefaultValidatorFactory());
    }

    /**
     * Create module using custom validator factory.
     * Useful if other (non guice) parts use validation too and don't want to know about guice.
     *
     * @param factory base factory
     */
    public AbstractValidationModule(final ValidatorFactory factory) {
        this.factory = factory;
    }

    @Override
    protected void configure() {
        final GuiceConstraintValidatorFactory constraintValidatorFactory = new GuiceConstraintValidatorFactory();
        requestInjection(constraintValidatorFactory);

        /* Overriding just constraints factory to allow them use guice injections */
        final Validator validator = factory.usingContext()
                .constraintValidatorFactory(constraintValidatorFactory)
                .getValidator();

        bind(Validator.class).toInstance(validator);
        bind(ExecutableValidator.class).toInstance(validator.forExecutables());
        bind(ValidatorFactory.class).toInstance(factory);

        final ValidationMethodInterceptor interceptor = new ValidationMethodInterceptor();
        requestInjection(interceptor);
        configureAop(interceptor);
    }

    /**
     * Called to apply aop bindings.
     *
     * @param interceptor validation method interceptor
     */
    protected abstract void configureAop(final ValidationMethodInterceptor interceptor);
}
