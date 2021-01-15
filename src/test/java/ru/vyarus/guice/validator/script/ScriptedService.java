package ru.vyarus.guice.validator.script;

import org.hibernate.validator.constraints.ParameterScriptAssert;

import javax.inject.Singleton;
import jakarta.validation.Valid;
import jakarta.validation.executable.ValidateOnExecution;
import java.util.List;

/**
 * Service with scripted validations
 *
 * @author Vyacheslav Rusakov
 * @since 25.06.2014
 */
@Singleton
@ValidateOnExecution
public class ScriptedService {

    @ParameterScriptAssert(lang = "javascript", script = "arg0.size() == arg1")
    public void paramsValid(List<Integer> list, int count) {
    }


    public void validBean(@Valid ScriptedBean bean) {
    }
}
