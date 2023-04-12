package ru.vyarus.guice.validator.simple;

import com.google.inject.Singleton;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.executable.ValidateOnExecution;

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
