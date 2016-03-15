package ru.vyarus.guice.validator.group.support.simple;

import ru.vyarus.guice.validator.group.support.groups.FooGroup;
import ru.vyarus.guice.validator.group.support.groups.FooGroup2;
import ru.vyarus.guice.validator.group.support.groups.ann.Group1;
import ru.vyarus.guice.validator.group.support.groups.ann.Group2;
import ru.vyarus.guice.validator.group.annotation.ValidationGroups;

/**
 * @author Vyacheslav Rusakov
 * @since 09.03.2016
 */
public class SimpleAnnService {

    public void nothing() {
    }

    @ValidationGroups(FooGroup.class)
    public void single() {
    }

    @ValidationGroups({FooGroup.class, FooGroup2.class})
    public void multiple() {
    }

    @Group1
    public void custom() {
    }

    @Group1
    @Group2
    public void custom2() {
    }
}
