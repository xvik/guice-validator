package ru.vyarus.guice.validator.customtype;

import com.google.inject.Singleton;
import jakarta.validation.Valid;
import jakarta.validation.executable.ValidateOnExecution;

/**
 * Service, used in custom validator, wired with guice.
 *
 * @author Vyacheslav Rusakov
 * @since 24.06.2014
 */
@Singleton
public class CustomService {

    public String getRequiredValue() {
        return "perfect";
    }

    /* @Valid will trigger bean validation with includes entire bean validation. */
    @ValidateOnExecution
    public void doAction(@Valid ComplexBean bean) {
    }
}
