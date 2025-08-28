package ru.vyarus.guice.validator.group.aop;

import com.google.inject.Inject;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import ru.vyarus.guice.validator.group.ValidationContext;
import ru.vyarus.guice.validator.group.annotation.MethodGroupsFactory;

/**
 * Intercepts methods under {@link ru.vyarus.guice.validator.group.annotation.ValidationGroups} annotation to declare
 * validation groups in scope of annotated method call.
 *
 * @author Vyacheslav Rusakov
 * @see ValidationContext
 * @since 07.03.2016
 */
public class ValidationGroupInterceptor implements MethodInterceptor {

    @Inject
    private ValidationContext context;
    @Inject
    private MethodGroupsFactory factory;

    @Override
    public Object invoke(final MethodInvocation invocation) {
        final Class<?>[] groups = factory.create(invocation.getMethod());
        return context.doWithGroups(invocation::proceed, groups);
    }
}
