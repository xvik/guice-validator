package ru.vyarus.guice.validator.customtype;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * Annotation enables validation for entire {@code ComplexBean}
 *
 * @author Vyacheslav Rusakov
 * @since 24.06.2014
 */
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {ComplexBeanValidator.class})
@Documented
public @interface ComplexBeanValid {
    /* ideally there should be just localization key, but for simplicity just message */
    String message() default "Bean is not valid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
