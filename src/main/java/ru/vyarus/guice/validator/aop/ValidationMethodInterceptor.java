package ru.vyarus.guice.validator.aop;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ElementKind;
import javax.validation.Path;
import javax.validation.executable.ExecutableValidator;
import java.lang.reflect.Member;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

/**
 * Method interceptor (actual method validation implementation).
 * <p>
 * Based on {@code org.hibernate.validator.internal.cdi.interceptor.ValidationInterceptor}
 * from hibernate-validator-cdi-5.1.1.Final module.
 * </p>
 *
 * @author hibernate-validator team
 * @since 24.06.2014
 */
@Singleton
public class ValidationMethodInterceptor implements MethodInterceptor {

    @Inject
    private ExecutableValidator validator;

    @Override
    public Object invoke(final MethodInvocation ctx) throws Throwable {
        Set<ConstraintViolation<Object>> violations = validator.validateParameters(
                ctx.getThis(), ctx.getMethod(), ctx.getArguments()
        );

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(
                    getMessage(ctx.getMethod(), ctx.getArguments(), violations), violations);
        }

        final Object result = ctx.proceed();

        violations = validator.validateReturnValue(
                ctx.getThis(), ctx.getMethod(), result
        );

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(
                    getMessage(ctx.getMethod(), ctx.getArguments(), violations), violations);
        }

        return result;
    }

    @SuppressWarnings("PMD.ConsecutiveLiteralAppends")
    private String getMessage(final Member member, final Object[] args,
                              final Set<? extends ConstraintViolation<?>> violations) {
        final StringBuilder message = new StringBuilder(200)
                .append(violations.size())
                .append(" constraint violation(s) occurred during method validation.")
                .append("\nConstructor or Method: ").append(member)
                .append("\nArgument values: ").append(Arrays.toString(args))
                .append("\nConstraint violations: ");
        int i = 1;
        for (ConstraintViolation<?> constraintViolation : violations) {
            final Path.Node leafNode = getLeafNode(constraintViolation);
            message.append("\n (")
                    .append(i++)
                    .append(") Kind: ")
                    .append(leafNode.getKind());
            if (leafNode.getKind() == ElementKind.PARAMETER) {
                message.append("\n parameter index: ")
                        .append(leafNode.as(Path.ParameterNode.class).getParameterIndex());
            }
            message.append("\n message: ").append(constraintViolation.getMessage())
                    .append("\n root bean: ").append(constraintViolation.getRootBean())
                    .append("\n property path: ").append(constraintViolation.getPropertyPath())
                    .append("\n constraint: ").append(constraintViolation.getConstraintDescriptor().getAnnotation());
        }

        return message.toString();
    }

    private Path.Node getLeafNode(final ConstraintViolation<?> constraintViolation) {
        final Iterator<Path.Node> nodes = constraintViolation.getPropertyPath().iterator();
        Path.Node leafNode = null;
        while (nodes.hasNext()) {
            leafNode = nodes.next();
        }
        return leafNode;
    }
}
