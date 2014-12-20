package ru.vyarus.guice.validator;

import com.google.inject.matcher.Matcher;
import com.google.inject.matcher.Matchers;
import ru.vyarus.guice.validator.aop.ValidationMethodInterceptor;
import ru.vyarus.guice.validator.aop.ValidatedMethodMatcher;

import javax.validation.ValidatorFactory;

/**
 * Implicit validation activation.
 * Validation executed when {@code @Valid} or any {@code @Constraint} annotation found on method or method parameter.
 *
 * @author Vyacheslav Rusakov
 * @since 19.12.2014
 */
public class ImplicitValidationModule extends AbstractValidationModule {

    private Matcher<? super Class<?>> classMatcher = Matchers.any();

    /**
     * Creates module using default validator factory.
     */
    public ImplicitValidationModule() {
        // default validation factory
    }

    /**
     * Create module using custom validator factory.
     * Useful if other (non guice) parts use validation too and don't want to know about guice.
     *
     * @param factory base factory
     */
    public ImplicitValidationModule(final ValidatorFactory factory) {
        super(factory);
    }

    /**
     * Specifies class matcher. Useful to exclude some types from aop matching.
     * By default, all types matched.
     *
     * @param classMatcher class matcher
     * @return module instance
     */
    public ImplicitValidationModule withMatcher(final Matcher<? super Class<?>> classMatcher) {
        this.classMatcher = classMatcher;
        return this;
    }

    @Override
    protected void configureAop(final ValidationMethodInterceptor interceptor) {
        bindInterceptor(classMatcher, new ValidatedMethodMatcher(), interceptor);
    }
}
