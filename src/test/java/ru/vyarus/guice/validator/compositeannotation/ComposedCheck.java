package ru.vyarus.guice.validator.compositeannotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.ReportAsSingleViolation;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.lang.annotation.*;

/**
 * Example of validation annotation composed of other annotations
 *
 * @author Vyacheslav Rusakov
 * @since 25.06.2014
 */
@NotNull
@Size(min = 2, max = 14)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {})
@Documented
@ReportAsSingleViolation //optional
public @interface ComposedCheck {
    String message() default "Composed check failed";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
