package ru.vyarus.guice.validator.group;

import com.google.common.base.Throwables;

import javax.inject.Singleton;
import javax.validation.groups.Default;
import java.util.*;

/**
 * Defines validation groups used by method validation.
 * Actual groups are defined with {@link ru.vyarus.guice.validator.group.annotation.ValidationGroups}
 * annotation.
 * <p/>
 * Context is thread bound.
 * <p/>
 * Inline contexts inherit all groups from upper levels.
 * <p/>
 * Default group is implicitly appended if allowed by module configuration (default true): see
 * {@link ru.vyarus.guice.validator.AbstractValidationModule#alwaysAddDefaultGroup(boolean)}
 * <p/>
 * Groups may be defined directly (without annotations usage) by using
 * {@link ValidationContext#doWithGroups(ru.vyarus.guice.validator.group.GroupAction, java.lang.Class[])}.
 *
 * @author Vyacheslav Rusakov
 * @since 07.03.2016
 */
@Singleton
public class ValidationContext {

    private static final Class<?>[] EMPTY = new Class<?>[0];
    private final ThreadLocal<List<Class<?>[]>> threadContext = new ThreadLocal<List<Class<?>[]>>();

    /**
     * @return current context validation groups or empty array when no groups defined
     */
    public Class<?>[] getContextGroups() {
        Class<?>[] res = EMPTY;
        final List<Class<?>[]> context = threadContext.get();
        if (context != null) {
            res = context.get(context.size() - 1);
        }
        return res;
    }

    /**
     * Defines context validation groups. Context is defined for all logic inside action callback
     * (in current thread).
     * Note: does not override current context groups.
     *
     * @param action action callback to be executed with validation groups
     * @param groups validation groups to use
     * @param <T>    action return type
     * @return object produced by action callback
     */
    public <T> T doWithGroups(final GroupAction<T> action, final Class<?>... groups) {
        pushContext(groups);
        try {
            return action.call();
        } catch (Throwable ex) {
            throw Throwables.propagate(ex);
        } finally {
            popContext();
        }
    }

    @SuppressWarnings("PMD.UseVarargs")
    private void pushContext(final Class<?>[] groups) {
        List<Class<?>[]> context = threadContext.get();
        if (context == null) {
            context = new ArrayList<Class<?>[]>();
            threadContext.set(context);
        }
        // remove duplicates
        final Set<Class<?>> allgroups = new LinkedHashSet<Class<?>>();
        if (!context.isEmpty()) {
            Collections.addAll(allgroups, context.get(context.size() - 1));
            // default group will always be last (here it comes from upper context and must be removed)
            allgroups.remove(Default.class);
        }
        Collections.addAll(allgroups, groups);
        context.add(allgroups.toArray(new Class<?>[allgroups.size()]));
    }

    @SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
    private void popContext() {
        final List<Class<?>[]> context = threadContext.get();
        if (context.size() > 1) {
            context.remove(context.size() - 1);
        } else {
            threadContext.remove();
        }
    }
}
