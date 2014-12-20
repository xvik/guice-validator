package ru.vyarus.guice.validator.matcher;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Sample annotation used to disable validation
 *
 * @author Vyacheslav Rusakov
 * @since 20.12.2014
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SuppressValidation {
}
