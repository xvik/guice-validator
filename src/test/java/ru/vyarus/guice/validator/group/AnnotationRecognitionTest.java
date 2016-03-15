package ru.vyarus.guice.validator.group;

import org.junit.Assert;
import org.junit.Test;
import ru.vyarus.guice.validator.group.annotation.GroupUtils;
import ru.vyarus.guice.validator.group.annotation.ValidationGroups;
import ru.vyarus.guice.validator.group.support.simple.*;

import java.util.List;

/**
 * @author Vyacheslav Rusakov
 * @since 09.03.2016
 */
public class AnnotationRecognitionTest {

    @Test
    public void checkSimpleCases() throws Exception {
        List<ValidationGroups> res = GroupUtils.findAnnotations(SimpleAnnService.class.getMethod("nothing"));
        Assert.assertEquals(0, res.size());

        res = GroupUtils.findAnnotations(SimpleAnnService.class.getMethod("single"));
        Assert.assertEquals(1, res.size());

        res = GroupUtils.findAnnotations(SimpleAnnService.class.getMethod("multiple"));
        Assert.assertEquals(1, res.size());

        res = GroupUtils.findAnnotations(SimpleAnnService.class.getMethod("custom"));
        Assert.assertEquals(1, res.size());

        res = GroupUtils.findAnnotations(SimpleAnnService.class.getMethod("custom2"));
        Assert.assertEquals(2, res.size());
    }

    @Test
    public void checkCompositeCases() throws Exception {
        List<ValidationGroups> res = GroupUtils.findAnnotations(CompositeAnnService.class.getMethod("nothing"));
        Assert.assertEquals(1, res.size());

        res = GroupUtils.findAnnotations(CompositeAnnService.class.getMethod("single"));
        Assert.assertEquals(2, res.size());

        res = GroupUtils.findAnnotations(CompositeAnnService.class.getMethod("multiple"));
        Assert.assertEquals(2, res.size());

        res = GroupUtils.findAnnotations(CompositeAnnService.class.getMethod("custom"));
        Assert.assertEquals(2, res.size());

        res = GroupUtils.findAnnotations(CompositeAnnService.class.getMethod("custom2"));
        Assert.assertEquals(3, res.size());
    }

    @Test
    public void checkCompositeCases2() throws Exception {
        List<ValidationGroups> res = GroupUtils.findAnnotations(CompositeAnnService2.class.getMethod("nothing"));
        Assert.assertEquals(1, res.size());

        res = GroupUtils.findAnnotations(CompositeAnnService2.class.getMethod("single"));
        Assert.assertEquals(2, res.size());

        res = GroupUtils.findAnnotations(CompositeAnnService2.class.getMethod("custom"));
        Assert.assertEquals(2, res.size());
    }

    @Test
    public void checkCompositeCases3() throws Exception {
        List<ValidationGroups> res = GroupUtils.findAnnotations(CompositeAnnService3.class.getMethod("nothing"));
        Assert.assertEquals(2, res.size());

        res = GroupUtils.findAnnotations(CompositeAnnService3.class.getMethod("single"));
        Assert.assertEquals(3, res.size());

        res = GroupUtils.findAnnotations(CompositeAnnService3.class.getMethod("custom"));
        Assert.assertEquals(3, res.size());
    }

    @Test
    public void checkInheritance() throws Exception {
        List<ValidationGroups> res = GroupUtils.findAnnotations(InheritedService.class.getMethod("groupInherit"));
        Assert.assertEquals(1, res.size());

        res = GroupUtils.findAnnotations(InheritedService.class.getMethod("single"));
        Assert.assertEquals(1, res.size());

        res = GroupUtils.findAnnotations(NotInheritedService.class.getMethod("noGroups"));
        Assert.assertEquals(0, res.size());

        res = GroupUtils.findAnnotations(NotInheritedService.class.getMethod("custom"));
        Assert.assertEquals(0, res.size());
    }
}
