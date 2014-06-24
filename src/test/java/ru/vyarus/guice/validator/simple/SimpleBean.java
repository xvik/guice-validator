package ru.vyarus.guice.validator.simple;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

/**
 * @author Vyacheslav Rusakov
 * @since 24.06.2014
 */
public class SimpleBean {

    @NotBlank
    private String name;
    @NotNull
    private Integer value;

    public SimpleBean() {
    }

    public SimpleBean(String name, Integer value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
}
