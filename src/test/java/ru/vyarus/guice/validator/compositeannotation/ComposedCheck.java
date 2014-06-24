package ru.vyarus.guice.validator.compositeannotation;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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
