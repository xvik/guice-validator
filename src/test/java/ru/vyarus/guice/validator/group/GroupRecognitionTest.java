package ru.vyarus.guice.validator.group;

import com.google.common.collect.Sets;
import org.junit.Assert;
import org.junit.Test;
import ru.vyarus.guice.validator.group.annotation.MethodGroupsFactory;
import ru.vyarus.guice.validator.group.support.groups.FooGroup;
import ru.vyarus.guice.validator.group.support.groups.FooGroup2;
import ru.vyarus.guice.validator.group.support.groups.RootFooGroup;
import ru.vyarus.guice.validator.group.support.groups.ann.Group1;
import ru.vyarus.guice.validator.group.support.groups.ann.Group2;
import ru.vyarus.guice.validator.group.support.groups.ann.RootGroup;
import ru.vyarus.guice.validator.group.support.simple.*;

/**
 * @author Vyacheslav Rusakov
 * @since 11.03.2016
 */
@SuppressWarnings("unchecked")
public class GroupRecognitionTest {

    MethodGroupsFactory factory = new MethodGroupsFactory(false);

    @Test
    public void checkSimpleCases() throws Throwable {
        Assert.assertArrayEquals(new Class[0],
                factory.create(SimpleAnnService.class.getMethod("nothing")));

        Assert.assertArrayEquals(new Class[]{FooGroup.class},
                factory.create(SimpleAnnService.class.getMethod("single")));

        Assert.assertEquals(Sets.newHashSet(FooGroup.class, FooGroup2.class),
                Sets.newHashSet(factory.create(SimpleAnnService.class.getMethod("multiple"))));

        Assert.assertArrayEquals(new Class[]{Group1.class},
                factory.create(SimpleAnnService.class.getMethod("custom")));

        Assert.assertEquals(Sets.newHashSet(Group1.class, Group2.class),
                Sets.newHashSet(factory.create(SimpleAnnService.class.getMethod("custom2"))));
    }

    @Test
    public void checkCompositeCases() throws Throwable {
        Assert.assertArrayEquals(new Class[]{RootGroup.class},
                factory.create(CompositeAnnService.class.getMethod("nothing")));

        Assert.assertEquals(Sets.newHashSet(RootGroup.class, FooGroup.class),
                Sets.newHashSet(factory.create(CompositeAnnService.class.getMethod("single"))));

        Assert.assertEquals(Sets.newHashSet(RootGroup.class, FooGroup.class, FooGroup2.class),
                Sets.newHashSet(factory.create(CompositeAnnService.class.getMethod("multiple"))));

        Assert.assertEquals(Sets.newHashSet(RootGroup.class, Group1.class),
                Sets.newHashSet(factory.create(CompositeAnnService.class.getMethod("custom"))));

        Assert.assertEquals(Sets.newHashSet(RootGroup.class, Group1.class, Group2.class),
                Sets.newHashSet(factory.create(CompositeAnnService.class.getMethod("custom2"))));
    }

    @Test
    public void checkCompositeCases2() throws Throwable {
        Assert.assertArrayEquals(new Class[]{RootFooGroup.class},
                factory.create(CompositeAnnService2.class.getMethod("nothing")));

        Assert.assertEquals(Sets.newHashSet(RootFooGroup.class, FooGroup.class),
                Sets.newHashSet(factory.create(CompositeAnnService2.class.getMethod("single"))));

        Assert.assertEquals(Sets.newHashSet(RootFooGroup.class, Group1.class),
                Sets.newHashSet(factory.create(CompositeAnnService2.class.getMethod("custom"))));
    }

    @Test
    public void checkCompositeCases3() throws Throwable {
        Assert.assertEquals(Sets.newHashSet(RootGroup.class, RootFooGroup.class),
                Sets.newHashSet(factory.create(CompositeAnnService3.class.getMethod("nothing"))));

        Assert.assertEquals(Sets.newHashSet(RootGroup.class, RootFooGroup.class, FooGroup.class),
                Sets.newHashSet(factory.create(CompositeAnnService3.class.getMethod("single"))));

        Assert.assertEquals(Sets.newHashSet(RootGroup.class, RootFooGroup.class, Group1.class),
                Sets.newHashSet(factory.create(CompositeAnnService3.class.getMethod("custom"))));
    }

    @Test
    public void checkInheritance() throws Throwable {

        // @validationGroups is inheritable and propagate
        Assert.assertEquals(Sets.newHashSet(RootFooGroup.class),
                Sets.newHashSet(factory.create(InheritedService.class.getMethod("groupInherit"))));

        // custom annotation was not inheritable
        Assert.assertEquals(Sets.newHashSet(),
                Sets.newHashSet(factory.create(NotInheritedService.class.getMethod("noGroups"))));

        // annotations on methods are not inherit
        Assert.assertEquals(Sets.newHashSet(RootFooGroup.class),
                Sets.newHashSet(factory.create(InheritedService.class.getMethod("single"))));

        Assert.assertEquals(Sets.newHashSet(),
                Sets.newHashSet(factory.create(NotInheritedService.class.getMethod("custom"))));
    }
}
