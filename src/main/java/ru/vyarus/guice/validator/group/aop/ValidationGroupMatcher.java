package ru.vyarus.guice.validator.group.aop;

import com.google.inject.matcher.AbstractMatcher;
import ru.vyarus.guice.validator.group.annotation.GroupUtils;

import java.lang.reflect.Method;

/**
 * Matcher for {@link ValidationGroupInterceptor}. Applied for methods annotated with
 * {@link ru.vyarus.guice.validator.group.annotation.ValidationGroups} (or with annotation annotated with it) and
 * methods in annotated class.
 *
 * @author Vyacheslav Rusakov
 * @since 09.03.2016
 */
public class ValidationGroupMatcher extends AbstractMatcher<Method> {

    @Override
    public boolean matches(final Method method) {
        return !GroupUtils.findAnnotations(method).isEmpty();
    }
}
