package ru.vyarus.guice.validator.group;

import com.google.common.collect.Sets;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.Assert;
import org.junit.Test;
import ru.vyarus.guice.validator.ImplicitValidationModule;
import ru.vyarus.guice.validator.group.support.PropFunction;
import ru.vyarus.guice.validator.group.support.groups.ann.Group1;
import ru.vyarus.guice.validator.group.support.model.Model;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.groups.Default;
import java.util.Set;

/**
 * @author Vyacheslav Rusakov
 * @since 11.03.2016
 */
public class NoDefaultGroupTest {

    @Test
    public void testDefaultGroupDisable() throws Exception {
        final Injector injector = Guice.createInjector(new ImplicitValidationModule()
                .alwaysAddDefaultGroup(false));
        final Service service = injector.getInstance(Service.class);

        // default group not applied - no validation errors
        service.call(new Model("sample", null, null));

        // verify default group would fail
        injector.getInstance(ValidationContext.class).doWithGroups(new GroupAction<Object>() {
            @Override
            public Object call() throws Throwable {
                try {
                    service.call(new Model("sample", null, null));
                } catch (ConstraintViolationException ex) {
                    Set<String> props = PropFunction.convert(ex.getConstraintViolations());
                    Assert.assertEquals(1, props.size());
                    Assert.assertEquals(Sets.newHashSet("def"), props);
                }
                return null;
            }
        }, Default.class);
    }

    public static class Service {

        // group required, otherwise default group used implicitly (when no groups)
        @Group1
        void call(@Valid Model model) {
        }
    }
}
