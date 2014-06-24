package ru.vyarus.guice.validator.customtype;

import javax.validation.constraints.NotNull;

/**
 * Bean with custom validator for entire class.
 *
 * @author Vyacheslav Rusakov
 * @since 24.06.2014
 */
@ComplexBeanValid
public class ComplexBean {

    @NotNull
    private String user;
    private int value;

    public ComplexBean() {
    }

    public ComplexBean(String user, int value) {
        this.user = user;
        this.value = value;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
