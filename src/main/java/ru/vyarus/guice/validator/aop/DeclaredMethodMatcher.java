package ru.vyarus.guice.validator.aop;

import com.google.inject.matcher.AbstractMatcher;

import java.lang.reflect.Method;

/**
 * Matcher filters synthetic and bridge methods from aop matching. Otherwise, guice aop will show warnings
 * on synthetic methods calls.
 *
 * @author Vyacheslav Rusakov
 * @since 24.12.2019
 */
public class DeclaredMethodMatcher extends AbstractMatcher<Method> {

    @Override
    public boolean matches(final Method method) {
        return !method.isSynthetic() || !method.isBridge();
    }
}
