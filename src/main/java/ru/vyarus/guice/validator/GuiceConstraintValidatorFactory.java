package ru.vyarus.guice.validator;

import com.google.inject.Injector;

import javax.inject.Inject;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorFactory;

public class GuiceConstraintValidatorFactory implements ConstraintValidatorFactory{

    @Inject
    private Injector injector;

    @Override
    public <T extends ConstraintValidator<?, ?>> T getInstance(Class<T> key) {
        // by default all beans are in prototype scope, so new instance will be obtained each time.
        // implementer may do singletons and maintain internal state (to re-use validators and simplify life for GC)
        return injector.getInstance(key);
    }

    @Override
    public void releaseInstance(ConstraintValidator<?, ?> instance) {
        // garbage collector will do it
    }
}
