package ru.vyarus.guice.validator.crossparams;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraintvalidation.SupportedValidationTarget;
import javax.validation.constraintvalidation.ValidationTarget;

/**
 * Validates method parameters.
 *
 * @author Vyacheslav Rusakov
 * @since 24.06.2014
 */
@SupportedValidationTarget(ValidationTarget.PARAMETERS)
public class CrossParamsValidator implements ConstraintValidator<CrossParamsCheck, Object[]> {

    @Override
    public void initialize(CrossParamsCheck constraintAnnotation) {

    }

    @Override
    public boolean isValid(Object[] value, ConstraintValidatorContext context) {
        Integer param1 = (Integer) value[0];
        Object param2 = value[1];
        return param1 != null && param1 == 1 && param2 instanceof Integer;
    }
}
