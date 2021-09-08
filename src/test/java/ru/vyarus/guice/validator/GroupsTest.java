package ru.vyarus.guice.validator;

import com.google.common.collect.Sets;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runners.Parameterized;
import ru.vyarus.guice.validator.group.ValidationContext;
import ru.vyarus.guice.validator.group.support.PropFunction;
import ru.vyarus.guice.validator.group.support.groups.ann.Group1;
import ru.vyarus.guice.validator.group.support.groups.ann.Group2;
import ru.vyarus.guice.validator.group.support.model.Model;

import javax.inject.Inject;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.executable.ValidateOnExecution;
import jakarta.validation.groups.Default;
import java.util.Collection;
import java.util.Set;

/**
 * @author Vyacheslav Rusakov
 * @since 11.03.2016
 */
public class GroupsTest extends AbstractParameterizedTest<GroupsTest.Service> {

    public GroupsTest(String type, Service service) {
        super(type, service);
    }

    @Parameterized.Parameters(name = "{index}: {0}")
    public static Collection<Object[]> generateData() {
        return AbstractParameterizedTest.generateData(Service.class);
    }

    @Test
    public void testNoContext() throws Exception {
        // ok
        service.noContext(new Model(null, null, "sample"));
        Assert.assertTrue(service.lastCallGroups.length == 0);

        try {
            // default group
            service.noContext(new Model(null, null, null));
            Assert.fail();
        } catch (ConstraintViolationException ex) {
            Set<String> props = PropFunction.convert(ex.getConstraintViolations());
            Assert.assertEquals(1, props.size());
            Assert.assertEquals(Sets.newHashSet("def"), props);
        }
    }

    @Test
    public void testContext1() throws Exception {
        // ok
        service.context1(new Model("sample", null, "sample"));
        Assert.assertArrayEquals(new Class<?>[]{Group1.class, Default.class}, service.lastCallGroups);

        try {
            service.context1(new Model(null, null, "sample"));
            Assert.fail();
        } catch (ConstraintViolationException ex) {
            Set<String> props = PropFunction.convert(ex.getConstraintViolations());
            Assert.assertEquals(1, props.size());
            Assert.assertEquals(Sets.newHashSet("foo"), props);
        }

        // default group is implicitly activated by default
        try {
            service.context1(new Model(null, null, null));
            Assert.fail();
        } catch (ConstraintViolationException ex) {
            Set<String> props = PropFunction.convert(ex.getConstraintViolations());
            Assert.assertEquals(2, props.size());
            Assert.assertEquals(Sets.newHashSet("foo", "def"), props);
        }
    }


    @Test
    public void testMultipleContexts() throws Exception {
        // ok
        service.multipleContexts(new Model("sample", "sample", "sample"));
        Assert.assertArrayEquals(new Class<?>[]{Group1.class, Group2.class, Default.class}, service.lastCallGroups);

        try {
            service.multipleContexts(new Model(null, null, null));
            Assert.fail();
        } catch (ConstraintViolationException ex) {
            Set<String> props = PropFunction.convert(ex.getConstraintViolations());
            Assert.assertEquals(3, props.size());
            Assert.assertEquals(Sets.newHashSet("foo", "bar", "def"), props);
        }
    }

    @Test
    public void testContextTree() throws Exception {
        service.contextTree(new Model("sample", "sample", "sample"));
        Assert.assertArrayEquals(new Class<?>[]{Group2.class, Group1.class, Default.class}, service.lastCallGroups);

        try {
            service.contextTree(new Model(null, "sample", "sample"));
            Assert.fail();
        } catch (ConstraintViolationException ex) {
            Set<String> props = PropFunction.convert(ex.getConstraintViolations());
            Assert.assertEquals(1, props.size());
            Assert.assertEquals(Sets.newHashSet("foo"), props);
        }
    }


    @Test
    public void testDuplicateGroups() throws Exception {
        service.duplicateGroups(new Model("sample", "sample", "sample"));
        Assert.assertArrayEquals(new Class<?>[]{Group1.class, Group2.class, Default.class}, service.lastCallGroups);
    }

    @ValidateOnExecution
    public static class Service {

        @Inject
        private ValidationContext context;

        public Class<?>[] lastCallGroups;

        public void noContext(@Valid Model model) {
            lastCallGroups = context.getContextGroups();
        }

        @Group1
        public void context1(@Valid Model model) {
            lastCallGroups = context.getContextGroups();
        }

        @Group1
        @Group2
        public void multipleContexts(@Valid Model model) {
            lastCallGroups = context.getContextGroups();
        }

        @Group2
        public void contextTree(@Valid Model model) {
            context1(model);
        }

        @Group1
        public void duplicateGroups(@Valid Model model) {
            contextTree(model);
        }
    }
}
