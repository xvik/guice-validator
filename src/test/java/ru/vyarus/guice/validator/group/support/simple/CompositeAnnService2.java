package ru.vyarus.guice.validator.group.support.simple;

import ru.vyarus.guice.validator.group.annotation.ValidationGroups;
import ru.vyarus.guice.validator.group.support.groups.FooGroup;
import ru.vyarus.guice.validator.group.support.groups.ann.Group1;
import ru.vyarus.guice.validator.group.support.groups.RootFooGroup;

/**
 * @author Vyacheslav Rusakov
 * @since 09.03.2016
 */
@ValidationGroups(RootFooGroup.class)
public class CompositeAnnService2 {

    public void nothing() {
    }

    @ValidationGroups(FooGroup.class)
    public void single() {
    }

    @Group1
    public void custom() {
    }
}
