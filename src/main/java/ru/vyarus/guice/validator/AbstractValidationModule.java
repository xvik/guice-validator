package ru.vyarus.guice.validator;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;
import com.google.inject.name.Names;
import ru.vyarus.guice.validator.aop.ValidationMethodInterceptor;
import ru.vyarus.guice.validator.constraint.GuiceConstraintValidatorFactory;
import ru.vyarus.guice.validator.group.aop.ValidationGroupInterceptor;
import ru.vyarus.guice.validator.group.aop.ValidationGroupMatcher;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.executable.ExecutableValidator;

/**
 * Base for validation modules. Binds {@link javax.validation.Validator},
 * {@link javax.validation.executable.ExecutableValidator} and
 * {@link javax.validation.ValidatorFactory} instances to context.
 * Specific module implementation provides validation aop bindings (but groups aop configured explicitly).
 *
 * @author Vyacheslav Rusakov
 * @since 19.12.2014
 */
public abstract class AbstractValidationModule extends AbstractModule {

    private ValidatorFactory factory;
    private boolean addDefaultGroup = true;

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

    /**
     * If set to true then default group ({@link javax.validation.groups.Default}) will be always added to groups
     * defined with {@link ru.vyarus.guice.validator.group.annotation.ValidationGroups} annotation.
     * When set to false only groups from annotation will be used (in this case all validation annotation without
     * explicit group definition (assumed with default group) will not be checked if default group wasn't defined
     * explicitly.
     * <p>
     * True by default (default group always added)
     *
     * @param addDefaultGroup true to enable implicit use of default group, false to use only defined groups
     * @param <T>             actual module type
     * @return module instance for chained calls
     */
    @SuppressWarnings("unchecked")
    public <T extends AbstractValidationModule> T alwaysAddDefaultGroup(final boolean addDefaultGroup) {
        this.addDefaultGroup = addDefaultGroup;
        return (T) this;
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

        bindConstant().annotatedWith(Names.named("guice.validator.addDefaultGroup")).to(addDefaultGroup);
        final ValidationGroupInterceptor groupInterceptor = new ValidationGroupInterceptor();
        requestInjection(groupInterceptor);
        configureGroupsAop(groupInterceptor);

        final ValidationMethodInterceptor interceptor = new ValidationMethodInterceptor();
        requestInjection(interceptor);
        configureAop(interceptor);
    }

    /**
     * Called to apply validation groups aop (recognized by
     * {@link ru.vyarus.guice.validator.group.annotation.ValidationGroups} annotation).
     *
     * @param interceptor validation groups method interceptor
     */
    protected void configureGroupsAop(final ValidationGroupInterceptor interceptor) {
        bindInterceptor(Matchers.any(), new ValidationGroupMatcher(), interceptor);
    }

    /**
     * Called to apply aop bindings.
     *
     * @param interceptor validation method interceptor
     */
    protected abstract void configureAop(final ValidationMethodInterceptor interceptor);
}
