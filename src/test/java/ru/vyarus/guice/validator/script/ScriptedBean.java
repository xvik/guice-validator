package ru.vyarus.guice.validator.script;

import org.hibernate.validator.constraints.ScriptAssert;

import java.util.Date;

/**
 * Example of script validation for bean state.
 *
 * @author Vyacheslav Rusakov
 * @since 25.06.2014
 */
@ScriptAssert(lang = "javascript", script = "it.start.before(it.finish)", alias = "it")
public class ScriptedBean {

    private Date start;
    private Date finish;

    public ScriptedBean() {
    }

    public ScriptedBean(Date start, Date finish) {
        this.start = start;
        this.finish = finish;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getFinish() {
        return finish;
    }

    public void setFinish(Date finish) {
        this.finish = finish;
    }
}
