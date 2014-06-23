package ru.vyarus.guice.validator.module;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

/**
 * @author Vyacheslav Rusakov (vyarus@gmail.com)
 * @since 24.06.2014.
 */
public class TestBean {

    @NotBlank
    private String name;
    @NotNull
    private Integer value;

    public TestBean() {
    }

    public TestBean(String name, Integer value) {
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
