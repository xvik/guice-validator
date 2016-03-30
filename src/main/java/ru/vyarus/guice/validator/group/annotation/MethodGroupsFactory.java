package ru.vyarus.guice.validator.group.annotation;

import com.google.common.base.Preconditions;
import com.google.inject.name.Named;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.validation.groups.Default;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Builds method validation groups context, by resolving {@link ValidationGroups} annotations.
 * Resolved contexts are cached for future re-use.
 * <p/>
 * Cache can be disabled using environment variable or system property
 * {@code System.setProperty(MethodGroupsFactory.CACHE_PROPERTY, 'false')} (or use
 * {@code MethodGroupsFactory#disableCache()}).
 * Property value checked on cache write. To clear current cache state use instance method:
 * {@link #clearCache()}.
 * <p/>
 * Default group is implicitly appended if allowed by module configuration (default true): see
 * {@link ru.vyarus.guice.validator.AbstractValidationModule#alwaysAddDefaultGroup(boolean)}.
 *
 * @author Vyacheslav Rusakov
 * @since 09.03.2016
 */
@Singleton
public class MethodGroupsFactory {

    /**
     * System property or environment variable name to disable cache.
     * If value is 'false' - cache disabled, otherwise cache enabled.
     */
    public static final String CACHE_PROPERTY = MethodGroupsFactory.class.getName() + ".cache";
    // lock will not affect performance for cached descriptors, just to make sure nothing was build two times
    private static final ReentrantLock LOCK = new ReentrantLock();

    private final Map<Method, Class<?>[]> cache = new HashMap<Method, Class<?>[]>();

    private final boolean addDefaultGroup;

    @Inject
    public MethodGroupsFactory(@Named("guice.validator.addDefaultGroup") final boolean addDefaultGroup) {
        this.addDefaultGroup = addDefaultGroup;
    }

    public Class<?>[] create(final Method method) {
        Class<?>[] groups = cache.get(method);
        if (groups == null) {
            LOCK.lock();
            try {
                // groups could be created while thread wait for LOCK
                groups = cache.get(method);
                if (groups == null) {
                    groups = buildGroups(method);
                    if (isCacheEnabled()) {
                        // internal check
                        Preconditions.checkState(cache.get(method) == null,
                                "Bad concurrency: groups already present in cache");
                        cache.put(method, groups);
                    }
                }
            } finally {
                LOCK.unlock();
            }
        }
        return groups;
    }

    private Class<?>[] buildGroups(final Method method) {
        final List<ValidationGroups> annotations = GroupUtils.findAnnotations(method);
        // remove duplicates
        final Set<Class<?>> result = new LinkedHashSet<Class<?>>();
        for (ValidationGroups group : annotations) {
            Collections.addAll(result, group.value());
        }
        if (addDefaultGroup) {
            result.add(Default.class);
        }
        return result.toArray(new Class<?>[result.size()]);
    }

    /**
     * Clears cached contexts (already parsed).
     * Cache could be completely disabled using system property or environment variable
     *
     * @see #CACHE_PROPERTY
     */
    public void clearCache() {
        LOCK.lock();
        try {
            cache.clear();
        } finally {
            LOCK.unlock();
        }
    }

    /**
     * Disables descriptors cache.
     */
    public static void disableCache() {
        System.setProperty(CACHE_PROPERTY, Boolean.FALSE.toString());
    }

    /**
     * @return true is cache enabled, false otherwise
     */
    public static boolean isCacheEnabled() {
        final String no = Boolean.FALSE.toString();
        return !no.equals(System.getenv(CACHE_PROPERTY))
                && !no.equals(System.getProperty(CACHE_PROPERTY));
    }
}
