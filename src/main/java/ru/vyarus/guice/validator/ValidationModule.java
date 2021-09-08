package ru.vyarus.guice.validator;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matcher;
import com.google.inject.matcher.Matchers;
import com.google.inject.name.Names;
import ru.vyarus.guice.validator.aop.DeclaredMethodMatcher;
import ru.vyarus.guice.validator.aop.ValidatedMethodMatcher;
import ru.vyarus.guice.validator.aop.ValidationMethodInterceptor;
import ru.vyarus.guice.validator.constraint.GuiceConstraintValidatorFactory;
import ru.vyarus.guice.validator.group.ValidationContext;
import ru.vyarus.guice.validator.group.aop.ValidationGroupInterceptor;
import ru.vyarus.guice.validator.group.aop.ValidationGroupMatcher;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.executable.ExecutableValidator;
import javax.validation.executable.ValidateOnExecution;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;

/**
 * Validation module. By default, validation executed when {@code @Valid} or any {@code @Constraint} annotation found
 * on method or method parameter (implicit mode). Alternatively, it could validate only annotated
 * modules (explicit mode): see {@link #validateAnnotatedOnly()}.
 * <p>
 * In both modes, target scope could be reduced using {@link #targetClasses(Matcher)} and
 * {@link #targetMethods(Matcher)}.
 * <p>
 * Binds {@link javax.validation.Validator}, {@link javax.validation.executable.ExecutableValidator} and
 * {@link javax.validation.ValidatorFactory} instances to context.
 * <p>
 * Validators are obtained from guice context (using custom {@link javax.validation.ConstraintValidatorFactory}, so
 * it is possible to use guice injections there.
 * <p>
 * Custom validation groups could be declared for target methods using
 * {@link ru.vyarus.guice.validator.group.annotation.ValidationGroups}.
 * By default, {@link javax.validation.groups.Default} group assumed to be always selected when custom validation
 * groups declared with {@link ru.vyarus.guice.validator.group.annotation.ValidationGroups}. To avoid implicit
 * default groups inclusion use {@link #strictGroupsDeclaration()}.
 *
 * @author Vyacheslav Rusakov
 * @since 24.06.2014
 */
public class ValidationModule extends AbstractModule {

    /**
     * Matches declared methods, ignoring synthetic and bridge methods (introduced by compiler and causing
     * execution warnings).
     */
    public static final Matcher<Method> DECLARED_METHOD_MATCHER = new DeclaredMethodMatcher();

    private final ValidatorFactory factory;

    private Matcher<? super Class<?>> classMatcher = Matchers.any();
    private Matcher<? super Method> methodMatcher = DECLARED_METHOD_MATCHER;
    private Class<? extends Annotation> validationAnnotation;
    private boolean addDefaultGroup = true;

    /**
     * Creates module with default validator factory.
     */
    public ValidationModule() {
        this(Validation.buildDefaultValidatorFactory());
    }

    /**
     * Create module with custom validator factory.
     * Useful if other (non guice) parts use validation too and don't want to know about guice.
     *
     * @param factory base factory
     */
    public ValidationModule(final ValidatorFactory factory) {
        this.factory = factory;
    }

    /**
     * By default, ({@link javax.validation.groups.Default}) group is always added to groups
     * defined with {@link ru.vyarus.guice.validator.group.annotation.ValidationGroups} annotation.
     * <p>
     * Calling this method disables default behavior: after calling it, {@link javax.validation.groups.Default}
     * must be explicitly declared.
     *
     * @return module instance for chained calls
     */
    public ValidationModule strictGroupsDeclaration() {
        this.addDefaultGroup = false;
        return this;
    }

    /**
     * Shortcut for {@link #validateAnnotatedOnly(Class)} to use default {@link ValidateOnExecution} annotation.
     * <p>
     * Note that such annotation usage contradict with its javadoc, but annotation name is ideal for this use case
     * (besides, introducing new general annotation is even worse option).
     *
     * @return module instance for chained calls
     */
    public ValidationModule validateAnnotatedOnly() {
        return validateAnnotatedOnly(ValidateOnExecution.class);
    }

    /**
     * Enables "explicit mode" when only methods annotated with provided annotation (and all methods in
     * annotated classes) are validated.
     * <p>
     * Explicit validation activation may be useful when validation annotations used just for compile time checks
     * (see hibernate validator apt lib). Moreover, it makes "automatic" validation more explicit.
     * <p>
     * Note that {@link #targetClasses(Matcher)} and {@link #targetMethods(Matcher)} could prevent method validation
     * (reduce scope).
     * <p>
     * There is a matching difference between modes: implicit mode matches only methods with detected validation
     * annotations, whereas in explicit, when target annotation declared on class, all methods are matched.
     *
     * @param annotation annotation to use
     * @return module instance for chained calls
     * @see #validateAnnotatedOnly() for default annotation ({@link ValidateOnExecution})
     */
    public ValidationModule validateAnnotatedOnly(final Class<? extends Annotation> annotation) {
        this.validationAnnotation = annotation;
        return this;
    }

    /**
     * Specifies class matcher. Useful to exclude some types from aop matching. Could be also used
     * to reduce scope in explicit mode {@link #validateAnnotatedOnly()}.
     * <p>
     * By default, all types matched.
     *
     * @param classMatcher class matcher
     * @return module instance for chained calls
     */
    public ValidationModule targetClasses(final Matcher<? super Class<?>> classMatcher) {
        this.classMatcher = classMatcher;
        return this;
    }

    /**
     * Specifies method matcher. Useful to exclude some methods from aop matching. Could be also used
     * to reduce scope in explicit mode {@link #validateAnnotatedOnly()}.
     * <p>
     * By default, all methods, except synthetic and bridge matched.
     *
     * @param methodMatcher method matcher
     * @return module instance for chained calls
     * @see DeclaredMethodMatcher for default matcher
     */
    public ValidationModule targetMethods(final Matcher<? super Method> methodMatcher) {
        this.methodMatcher = methodMatcher;
        return this;
    }

    @Override
    protected void configure() {
        final GuiceConstraintValidatorFactory constraintValidatorFactory = new GuiceConstraintValidatorFactory();
        requestInjection(constraintValidatorFactory);

        /* Overriding just constraints factory to allow them use guice injections */
        final Validator validator = factory.usingContext()
                .constraintValidatorFactory(constraintValidatorFactory)
                .getValidator();

        bind(Validator.class).toInstance(validator);
        bind(ExecutableValidator.class).toInstance(validator.forExecutables());
        // NOTE bound factory is not aware of guice! because it use default ConstraintValidatorFactory
        bind(ValidatorFactory.class).toInstance(factory);
        bind(ValidationContext.class);

        bindConstant().annotatedWith(Names.named("guice.validator.addDefaultGroup")).to(addDefaultGroup);
        final ValidationGroupInterceptor groupInterceptor = new ValidationGroupInterceptor();
        requestInjection(groupInterceptor);
        configureGroupsAop(groupInterceptor);

        final ValidationMethodInterceptor interceptor = new ValidationMethodInterceptor();
        requestInjection(interceptor);
        configureAop(interceptor);
    }

    /**
     * Called to apply validation groups aop (recognized by
     * {@link ru.vyarus.guice.validator.group.annotation.ValidationGroups} annotation).
     *
     * @param interceptor validation groups method interceptor
     */
    protected void configureGroupsAop(final ValidationGroupInterceptor interceptor) {
        bindInterceptor(Matchers.any(), new ValidationGroupMatcher(), interceptor);
    }

    /**
     * Called to apply aop bindings.
     *
     * @param interceptor validation method interceptor
     */
    protected void configureAop(final ValidationMethodInterceptor interceptor) {
        if (validationAnnotation != null) {
            // EXPLICIT MODE

            // all annotated methods
            bindInterceptor(classMatcher, getMethodMatcher(validationAnnotation), interceptor);
            // all methods in annotated beans (do not search for validation annotations!)
            bindInterceptor(getClassMatcher(validationAnnotation), methodMatcher, interceptor);
        } else {
            // IMPLICIT MODE

            // methods searched by validation annotations appearance
            bindInterceptor(classMatcher, getValidatedMethodMatcher(), interceptor);
        }
    }

    @SuppressWarnings({"unchecked", "PMD.CompareObjectsWithEquals"})
    protected Matcher<? super Class<?>> getClassMatcher(final Class<? extends Annotation> annotation) {
        final Matcher<AnnotatedElement> res = Matchers.annotatedWith(annotation);
        return classMatcher == Matchers.any()
                // combine custom filter with annotation
                ? res : res.and((Matcher<? super AnnotatedElement>) classMatcher);
    }

    @SuppressWarnings({"unchecked", "PMD.CompareObjectsWithEquals"})
    protected Matcher<? super Method> getMethodMatcher(final Class<? extends Annotation> annotation) {
        final Matcher<AnnotatedElement> res = Matchers.annotatedWith(annotation);
        return methodMatcher == DECLARED_METHOD_MATCHER
                // combine custom filter with annotation
                ? res : res.and((Matcher<? super AnnotatedElement>) methodMatcher);
    }

    @SuppressWarnings("PMD.CompareObjectsWithEquals")
    protected Matcher<? super Method> getValidatedMethodMatcher() {
        final Matcher<Method> res = new ValidatedMethodMatcher();
        // combine custom filter with annotation
        return methodMatcher == Matchers.any() ? res : res.and(methodMatcher);
    }
}
