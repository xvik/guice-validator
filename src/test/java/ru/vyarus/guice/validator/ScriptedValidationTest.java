package ru.vyarus.guice.validator;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.vyarus.guice.validator.script.ScriptedBean;
import ru.vyarus.guice.validator.script.ScriptedService;

import javax.validation.ConstraintViolationException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

/**
 * @author Vyacheslav Rusakov
 * @since 25.06.2014
 */
public class ScriptedValidationTest {
    private static ScriptedService scriptedService;

    @BeforeClass
    public static void setUp() throws Exception {
        Injector injector = Guice.createInjector(new ValidationModule());
        scriptedService = injector.getInstance(ScriptedService.class);
    }

    @Test(expected = ConstraintViolationException.class)
    public void testParamsFail() throws Exception {
        scriptedService.paramsValid(Collections.<Integer>emptyList(), 1);
    }

    @Test
    public void testParams() {
        scriptedService.paramsValid(Arrays.asList(1, 2, 3), 3);
    }

    @Test(expected = ConstraintViolationException.class)
    public void testScriptedParamFail() throws Exception {
        Date now = new Date();
        scriptedService.validBean(new ScriptedBean(now, now));
    }

    @Test
    public void testScriptedParam() {
        Date now = new Date();
        scriptedService.validBean(new ScriptedBean(now, new Date(now.getTime() + 10000)));
    }
}
