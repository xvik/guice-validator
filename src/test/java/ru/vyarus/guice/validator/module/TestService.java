package ru.vyarus.guice.validator.module;

import javax.inject.Singleton;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.executable.ValidateOnExecution;

/**
 * @author Vyacheslav Rusakov (vyarus@gmail.com)
 * @since 24.06.2014.
 */
@Singleton
@ValidateOnExecution
public class TestService {

    public TestBean beanRequired(@NotNull TestBean bean) {
        return null;
    }

    public TestBean validBeanRequired(@NotNull @Valid TestBean bean) {
        return null;
    }

    @NotNull
    public TestBean notNullReturn(TestBean bean) {
        return bean;
    }

    @NotNull
    @Valid
    public TestBean validReturn(TestBean bean) {
        return bean;
    }
}
