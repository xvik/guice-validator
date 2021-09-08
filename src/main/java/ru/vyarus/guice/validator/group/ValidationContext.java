package ru.vyarus.guice.validator.group;

import com.google.common.base.Throwables;
import com.google.inject.name.Named;
import ru.vyarus.guice.validator.ValidationModule;

import javax.inject.Inject;
import javax.inject.Singleton;
import jakarta.validation.groups.Default;
import java.util.*;

/**
 * Defines validation groups used by method validation.
 * Actual groups are defined with {@link ru.vyarus.guice.validator.group.annotation.ValidationGroups}
 * annotation.
 * <p>
 * Context is thread bound.
 * <p>
 * Inline contexts inherit all groups from upper levels.
 * <p>
 * Default group is implicitly appended if allowed by module configuration (default true): see
 * {@link ValidationModule#strictGroupsDeclaration()}
 * <p>
 * Groups may be defined directly (without annotations usage) by using
 * {@link ValidationContext#doWithGroups(ru.vyarus.guice.validator.group.GroupAction, java.lang.Class[])}.
 *
 * @author Vyacheslav Rusakov
 * @since 07.03.2016
 */
@Singleton
public class ValidationContext {

    private static final Class<?>[] EMPTY = new Class<?>[0];
    private final ThreadLocal<List<Class<?>[]>> threadContext = new ThreadLocal<>();

    private final boolean addDefaultGroup;

    @Inject
    public ValidationContext(@Named("guice.validator.addDefaultGroup") final boolean addDefaultGroup) {
        this.addDefaultGroup = addDefaultGroup;
    }

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
            Throwables.throwIfUnchecked(ex);
            throw new IllegalStateException(ex);
        } finally {
            popContext();
        }
    }

    @SuppressWarnings("PMD.UseVarargs")
    private void pushContext(final Class<?>[] groups) {
        List<Class<?>[]> context = threadContext.get();
        if (context == null) {
            context = new ArrayList<>();
            threadContext.set(context);
        }
        // remove duplicates
        final Set<Class<?>> allgroups = new LinkedHashSet<>();
        if (!context.isEmpty()) {
            Collections.addAll(allgroups, context.get(context.size() - 1));
            // should not be applied otherwise because default group could be added manually
            if (addDefaultGroup) {
                // default group will always be last (here it comes from upper context and must be removed)
                allgroups.remove(Default.class);
            }
        }
        Collections.addAll(allgroups, groups);
        context.add(allgroups.toArray(new Class<?>[0]));
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
