package ru.vyarus.guice.validator.matcher;

import jakarta.validation.constraints.NotNull;

/**
 * @author Vyacheslav Rusakov
 * @since 24.12.2019
 */
public class ExceptionalMethod {

    @SuppressValidation
    public void doSmth(@NotNull String val){

    }

    public void doSmth2(@NotNull String val){

    }
}
