package ru.vyarus.guice.validator.group.aop;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import ru.vyarus.guice.validator.group.GroupAction;
import ru.vyarus.guice.validator.group.ValidationContext;
import ru.vyarus.guice.validator.group.annotation.MethodGroupsFactory;

import com.google.inject.Inject;

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
    public Object invoke(final MethodInvocation invocation) throws Throwable {
        final Class<?>[] groups = factory.create(invocation.getMethod());
        return context.doWithGroups(new GroupAction<Object>() {
            @Override
            public Object call() throws Throwable {
                return invocation.proceed();
            }
        }, groups);
    }
}
