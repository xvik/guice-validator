package ru.vyarus.guice.validator;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.executable.ExecutableValidator;
import javax.validation.executable.ValidateOnExecution;

public class ValidationModule extends AbstractModule {

    private ValidatorFactory bootstrapFactory;

    public ValidationModule() {
        this(Validation.buildDefaultValidatorFactory());
    }

    public ValidationModule(ValidatorFactory bootstrapFactory) {
        this.bootstrapFactory = bootstrapFactory;
    }

    @Override
    protected void configure() {
        GuiceConstraintValidatorFactory constraintValidatorFactory = new GuiceConstraintValidatorFactory();
        requestInjection(constraintValidatorFactory);

        Validator validator = bootstrapFactory.usingContext()
                // allow to use guice bindings inside validators
                .constraintValidatorFactory(constraintValidatorFactory)
                .getValidator();

        bind(Validator.class).toInstance(validator);
        bind(ExecutableValidator.class).toInstance(validator.forExecutables());

        GuiceMethodValidator interceptor = new GuiceMethodValidator();
        requestInjection(interceptor);

        //think about Constraint.class, Valid.class, bindings (using ValidateOnExecution everywhere is actually not correct)
        bindInterceptor(Matchers.any(), Matchers.annotatedWith(ValidateOnExecution.class), interceptor);
        bindInterceptor(Matchers.annotatedWith(ValidateOnExecution.class), Matchers.any(), interceptor);
    }
}
