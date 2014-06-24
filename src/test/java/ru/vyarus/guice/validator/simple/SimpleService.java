package ru.vyarus.guice.validator.simple;

import javax.inject.Singleton;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.executable.ValidateOnExecution;

/**
 * Very basic method validations.
 *
 * @author Vyacheslav Rusakov
 * @since 24.06.2014.
 */
@Singleton
@ValidateOnExecution // enables validation for all methods
public class SimpleService {

    public SimpleBean beanRequired(@NotNull SimpleBean bean) {
        return null;
    }

    public SimpleBean validBeanRequired(@NotNull @Valid SimpleBean bean) {
        return null;
    }

    @NotNull
    public SimpleBean notNullReturn(SimpleBean bean) {
        return bean;
    }

    @NotNull
    @Valid
    public SimpleBean validReturn(SimpleBean bean) {
        return bean;
    }
}
