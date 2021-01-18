package ru.vyarus.guice.validator.group.support.model;

import ru.vyarus.guice.validator.group.support.groups.ann.Group1;
import ru.vyarus.guice.validator.group.support.groups.ann.Group2;

import jakarta.validation.constraints.NotNull;

/**
 * @author Vyacheslav Rusakov
 * @since 11.03.2016
 */
public class Model {

    @NotNull(groups = Group1.class)
    private String foo;

    @NotNull(groups = Group2.class)
    private String bar;

    @NotNull
    private String def;

    public Model(String foo, String bar, String def) {
        this.foo = foo;
        this.bar = bar;
        this.def = def;
    }

    public String getFoo() {
        return foo;
    }

    public void setFoo(String foo) {
        this.foo = foo;
    }

    public String getBar() {
        return bar;
    }

    public void setBar(String bar) {
        this.bar = bar;
    }

    public String getDef() {
        return def;
    }

    public void setDef(String def) {
        this.def = def;
    }
}
