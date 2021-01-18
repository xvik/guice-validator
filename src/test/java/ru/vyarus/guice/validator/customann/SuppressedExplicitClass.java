package ru.vyarus.guice.validator.customann;

import ru.vyarus.guice.validator.matcher.SuppressValidation;

import jakarta.validation.constraints.NotNull;

/**
 * @author Vyacheslav Rusakov
 * @since 24.12.2019
 */
@ToValidate
@SuppressValidation
public class SuppressedExplicitClass {

    public void doSmth(@NotNull String val) {

    }
}
