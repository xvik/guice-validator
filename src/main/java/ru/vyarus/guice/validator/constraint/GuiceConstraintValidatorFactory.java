package ru.vyarus.guice.validator.constraint;

import com.google.inject.Injector;

import com.google.inject.Inject;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorFactory;

/**
 * Creates validator instances with guice injections available.
 * Any type of injection is allowed (constructor, setter, field).
 * Pay attention that validator usually stateful, so not declare the as singletons
 * (better not declare at all and rely on guice automatic dependecy resolution).
 *
 * @author Vyacheslav Rusakov
 * @since 24.06.2014
 */
public class GuiceConstraintValidatorFactory implements ConstraintValidatorFactory {

    @Inject
    private Injector injector;

    @Override
    public <T extends ConstraintValidator<?, ?>> T getInstance(final Class<T> key) {
        /* By default, all beans are in prototype scope, so new instance will be obtained each time.
         Validator implementer may declare it as singleton and manually maintain internal state
         (to re-use validators and simplify life for GC) */
        return injector.getInstance(key);
    }

    @Override
    public void releaseInstance(final ConstraintValidator<?, ?> instance) {
        /* Garbage collector will do it */
    }
}
