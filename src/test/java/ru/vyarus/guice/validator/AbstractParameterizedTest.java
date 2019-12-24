package ru.vyarus.guice.validator;

import com.google.inject.Guice;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

/**
 * @author Vyacheslav Rusakov
 * @since 19.12.2014
 */
@RunWith(Parameterized.class)
public abstract class AbstractParameterizedTest<T> {

    public static final String EXPLICIT = "Explicit";
    public static final String IMPLICIT = "Implicit";
    T service;
    String type;

    public AbstractParameterizedTest(String type, T service) {
        this.service = service;
        this.type = type;
    }

    //    @Parameterized.Parameters(name = "{index}: {0}")
    public static Collection<Object[]> generateData(Class<?> type) {
        return Arrays.asList(new Object[][]{
                {EXPLICIT, Guice.createInjector(new ValidationModule().validateAnnotatedOnly()).getInstance(type)},
                {IMPLICIT, Guice.createInjector(new ValidationModule()).getInstance(type)}
        });
    }

    boolean isExplicit() {
        return type.equals(EXPLICIT);
    }
}
