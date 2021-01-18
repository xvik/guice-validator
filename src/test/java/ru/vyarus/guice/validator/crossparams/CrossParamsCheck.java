package ru.vyarus.guice.validator.crossparams;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * Annotation for method parameters validation.
 *
 * @author Vyacheslav Rusakov
 * @since 24.06.2014
 */
@Constraint(validatedBy = CrossParamsValidator.class)
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CrossParamsCheck {
    /* ideally there should be just localization key, but for simplicity just message */
    String message() default "Parameters are not valid";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
