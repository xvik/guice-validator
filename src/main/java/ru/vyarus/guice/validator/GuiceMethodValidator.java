package ru.vyarus.guice.validator;

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
 * @author hibernate-validator team
 * @since 24.06.2014
 */
@Singleton
public class GuiceMethodValidator implements MethodInterceptor {

    @Inject
    private ExecutableValidator validator;

    @Override
    public Object invoke(MethodInvocation ctx) throws Throwable {
        Set<ConstraintViolation<Object>> violations = validator.validateParameters(
                ctx.getThis(),
                ctx.getMethod(),
                ctx.getArguments()
        );

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(
                    getMessage(ctx.getMethod(), ctx.getArguments(), violations),
                    violations
            );
        }

        Object result = ctx.proceed();

        violations = validator.validateReturnValue(
                ctx.getThis(),
                ctx.getMethod(),
                result
        );

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(
                    getMessage(ctx.getMethod(), ctx.getArguments(), violations),
                    violations
            );
        }

        return result;
    }

    private String getMessage(Member member, Object[] args, Set<? extends ConstraintViolation<?>> violations) {

        StringBuilder message = new StringBuilder();
        message.append(violations.size());
        message.append(" constraint violation(s) occurred during method validation.");
        message.append("\nConstructor or Method: ");
        message.append(member);
        message.append("\nArgument values: ");
        message.append(Arrays.toString(args));
        message.append("\nConstraint violations: ");

        int i = 1;
        for (ConstraintViolation<?> constraintViolation : violations) {
            Path.Node leafNode = getLeafNode(constraintViolation);

            message.append("\n (");
            message.append(i);
            message.append(")");
            message.append(" Kind: ");
            message.append(leafNode.getKind());
            if (leafNode.getKind() == ElementKind.PARAMETER) {
                message.append("\n parameter index: ");
                message.append(leafNode.as(Path.ParameterNode.class).getParameterIndex());
            }
            message.append("\n message: ");
            message.append(constraintViolation.getMessage());
            message.append("\n root bean: ");
            message.append(constraintViolation.getRootBean());
            message.append("\n property path: ");
            message.append(constraintViolation.getPropertyPath());
            message.append("\n constraint: ");
            message.append(constraintViolation.getConstraintDescriptor().getAnnotation());

            i++;
        }

        return message.toString();
    }

    private Path.Node getLeafNode(ConstraintViolation<?> constraintViolation) {
        Iterator<Path.Node> nodes = constraintViolation.getPropertyPath().iterator();
        Path.Node leafNode = null;
        while (nodes.hasNext()) {
            leafNode = nodes.next();
        }
        return leafNode;
    }
}
