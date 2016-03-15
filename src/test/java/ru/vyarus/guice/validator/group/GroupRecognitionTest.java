package ru.vyarus.guice.validator.group;

import org.junit.Assert;
import org.junit.Test;
import ru.vyarus.guice.validator.group.annotation.MethodGroupsFactory;
import ru.vyarus.guice.validator.group.support.groups.FooGroup;
import ru.vyarus.guice.validator.group.support.groups.FooGroup2;
import ru.vyarus.guice.validator.group.support.groups.RootFooGroup;
import ru.vyarus.guice.validator.group.support.groups.ann.Group1;
import ru.vyarus.guice.validator.group.support.groups.ann.Group2;
import ru.vyarus.guice.validator.group.support.groups.ann.RootGroup;
import ru.vyarus.guice.validator.group.support.simple.CompositeAnnService;
import ru.vyarus.guice.validator.group.support.simple.CompositeAnnService2;
import ru.vyarus.guice.validator.group.support.simple.CompositeAnnService3;
import ru.vyarus.guice.validator.group.support.simple.SimpleAnnService;

import javax.validation.groups.Default;

/**
 * @author Vyacheslav Rusakov
 * @since 11.03.2016
 */
public class GroupRecognitionTest {

    MethodGroupsFactory factory = new MethodGroupsFactory(false);

    @Test
    public void checkSimpleCases() throws Throwable {
        Assert.assertArrayEquals(new Class[0],
                factory.create(SimpleAnnService.class.getMethod("nothing")));

        Assert.assertArrayEquals(new Class[]{FooGroup.class},
                factory.create(SimpleAnnService.class.getMethod("single")));

        Assert.assertArrayEquals(new Class[]{FooGroup.class, FooGroup2.class},
                factory.create(SimpleAnnService.class.getMethod("multiple")));

        Assert.assertArrayEquals(new Class[]{Group1.class},
                factory.create(SimpleAnnService.class.getMethod("custom")));

        Assert.assertArrayEquals(new Class[]{Group1.class, Group2.class},
                factory.create(SimpleAnnService.class.getMethod("custom2")));
    }

    @Test
    public void checkCompositeCases() throws Throwable {
        Assert.assertArrayEquals(new Class[]{RootGroup.class},
                factory.create(CompositeAnnService.class.getMethod("nothing")));

        Assert.assertArrayEquals(new Class[]{RootGroup.class, FooGroup.class},
                factory.create(CompositeAnnService.class.getMethod("single")));

        Assert.assertArrayEquals(new Class[]{RootGroup.class, FooGroup.class, FooGroup2.class},
                factory.create(CompositeAnnService.class.getMethod("multiple")));

        Assert.assertArrayEquals(new Class[]{RootGroup.class, Group1.class},
                factory.create(CompositeAnnService.class.getMethod("custom")));

        Assert.assertArrayEquals(new Class[]{RootGroup.class, Group1.class, Group2.class},
                factory.create(CompositeAnnService.class.getMethod("custom2")));
    }

    @Test
    public void checkCompositeCases2() throws Throwable {
        Assert.assertArrayEquals(new Class[]{RootFooGroup.class},
                factory.create(CompositeAnnService2.class.getMethod("nothing")));

        Assert.assertArrayEquals(new Class[]{RootFooGroup.class, FooGroup.class},
                factory.create(CompositeAnnService2.class.getMethod("single")));

        Assert.assertArrayEquals(new Class[]{RootFooGroup.class, Group1.class},
                factory.create(CompositeAnnService2.class.getMethod("custom")));
    }

    @Test
    public void checkCompositeCases3() throws Throwable {
        Assert.assertArrayEquals(new Class[]{RootGroup.class, RootFooGroup.class},
                factory.create(CompositeAnnService3.class.getMethod("nothing")));

        Assert.assertArrayEquals(new Class[]{RootGroup.class, RootFooGroup.class, FooGroup.class},
                factory.create(CompositeAnnService3.class.getMethod("single")));

        Assert.assertArrayEquals(new Class[]{RootGroup.class, RootFooGroup.class, Group1.class},
                factory.create(CompositeAnnService3.class.getMethod("custom")));
    }
}
