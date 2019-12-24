package ru.vyarus.guice.validator.customann;

import javax.validation.constraints.NotNull;

/**
 * @author Vyacheslav Rusakov
 * @since 24.12.2019
 */
public class ExplicitMethod {

    public void doSmth(@NotNull String val) {

    }

    @ToValidate
    public void doSmth2(@NotNull String val) {

    }
}
