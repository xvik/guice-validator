package ru.vyarus.guice.validator.group;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.vyarus.guice.validator.group.annotation.MethodGroupsFactory;
import ru.vyarus.guice.validator.group.support.simple.SimpleAnnService;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author Vyacheslav Rusakov
 * @since 13.03.2016
 */
public class GroupCacheTest {

    ExecutorService executor;
    MethodGroupsFactory factory = new MethodGroupsFactory(false);

    @Before
    public void setUp() throws Exception {
        factory.clearCache();
        executor = Executors.newFixedThreadPool(20);
    }

    @After
    public void tearDown() throws Exception {
        executor.shutdown();
    }

    @Test
    public void testCache() throws Throwable {
        final Method method = SimpleAnnService.class.getMethod("single");
        Class<?>[] groups = factory.create(method);
        Assert.assertTrue(groups == factory.create(method));
    }

    @Test
    public void testConcurrency() throws Exception {
        List<Future<?>> executed = new ArrayList<Future<?>>();
        final Method[] method = new Method[]{
                SimpleAnnService.class.getMethod("single"),
                SimpleAnnService.class.getMethod("multiple"),
                SimpleAnnService.class.getMethod("nothing"),
                SimpleAnnService.class.getMethod("custom")
        };
        int count = 30;
        for (int i = 0; i < count; i++) {
            executed.add(
                    executor.submit(new Runnable() {
                        @Override
                        public void run() {
                            factory.create(method[(int)(method.length*Math.random())]);
                        }
                    })
            );
        }
        for(Future<?> future: executed) {
            future.get();
        }
    }

    @Test
    public void testCacheMethods() throws Exception {
        Field field = MethodGroupsFactory.class.getDeclaredField("cache");
        field.setAccessible(true);
        Map cache = (Map) field.get(factory);

        Assert.assertTrue(cache.isEmpty());

        final Method method = SimpleAnnService.class.getMethod("single");
        factory.create(method);
        Assert.assertEquals(1, cache.size());

        Assert.assertTrue(MethodGroupsFactory.isCacheEnabled());
        factory.clearCache();
        Assert.assertTrue(MethodGroupsFactory.isCacheEnabled());
        Assert.assertTrue(cache.isEmpty());

        MethodGroupsFactory.disableCache();
        Assert.assertTrue(!MethodGroupsFactory.isCacheEnabled());
        Assert.assertTrue(cache.isEmpty());

        factory.create(method);
        Assert.assertTrue(cache.isEmpty());
    }
}
