package ru.vyarus.guice.validator;

import com.pushtorefresh.private_constructor_checker.PrivateConstructorChecker;
import org.junit.Test;
import ru.vyarus.guice.validator.group.annotation.GroupUtils;

/**
 * @author Vyacheslav Rusakov
 * @since 31.03.2016
 */
public class DeadCodeTest {

    @Test
    public void checkPrivateCtor() throws Exception {
        PrivateConstructorChecker
                .forClass(GroupUtils.class)
                .check();
    }
}
