package ru.vyarus.guice.validator.compositeannotation;

import javax.inject.Singleton;
import jakarta.validation.executable.ValidateOnExecution;

/**
 * Example of using composed annotation for parameter validation.(return value validation doesn't work, maybe bug.. investigation required)
 *
 * @author Vyacheslav Rusakov
 * @since 25.06.2014
 */
@Singleton
@ValidateOnExecution
public class ComposedCheckService {

    public String checkParam(@ComposedCheck String string) {
        return string;
    }

    // todo can't make composed annotation work for return value
//    @ComposedCheck
//    public String checkReturn(String string) {
//        return string;
//    }
}
