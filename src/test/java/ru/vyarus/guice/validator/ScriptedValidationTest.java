package ru.vyarus.guice.validator;

import org.junit.Test;
import org.junit.runners.Parameterized;
import ru.vyarus.guice.validator.script.ScriptedBean;
import ru.vyarus.guice.validator.script.ScriptedService;

import jakarta.validation.ConstraintViolationException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

/**
 * @author Vyacheslav Rusakov
 * @since 25.06.2014
 */
public class ScriptedValidationTest extends AbstractParameterizedTest<ScriptedService> {

    public ScriptedValidationTest(String type, ScriptedService service) {
        super(type, service);
    }

    @Parameterized.Parameters(name = "{index}: {0}")
    public static Collection<Object[]> generateData() {
        return AbstractParameterizedTest.generateData(ScriptedService.class);
    }

    @Test(expected = ConstraintViolationException.class)
    public void testParamsFail() throws Exception {
        service.paramsValid(Collections.<Integer>emptyList(), 1);
    }

    @Test
    public void testParams() {
        service.paramsValid(Arrays.asList(1, 2, 3), 3);
    }

    @Test(expected = ConstraintViolationException.class)
    public void testScriptedParamFail() throws Exception {
        Date now = new Date();
        service.validBean(new ScriptedBean(now, now));
    }

    @Test
    public void testScriptedParam() {
        Date now = new Date();
        service.validBean(new ScriptedBean(now, new Date(now.getTime() + 10000)));
    }
}
