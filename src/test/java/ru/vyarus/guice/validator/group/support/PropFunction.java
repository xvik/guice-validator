package ru.vyarus.guice.validator.group.support;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import javax.validation.ConstraintViolation;
import java.util.Collection;
import java.util.Set;

/**
 * @author Vyacheslav Rusakov
 * @since 11.03.2016
 */
public class PropFunction implements Function<ConstraintViolation, String> {

    @Override
    public String apply(ConstraintViolation input) {
        return Lists.newLinkedList(input.getPropertyPath()).getLast().toString();
    }

    public static Set<String> convert(Collection<ConstraintViolation<?>> violations) {
        return Sets.newHashSet(Iterables.transform(violations, new PropFunction()));
    }
}
