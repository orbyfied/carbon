package net.orbyfied.carbon.test;

import net.orbyfied.carbon.Carbon;
import net.orbyfied.carbon.logging.BukkitLogger;
import net.orbyfied.carbon.registry.Identifier;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestManager {

    final Carbon main;
    final BukkitLogger logger;

    final List<CompiledTest> tests = new ArrayList<>();

    final List<Class<?>> classesToCompile = new ArrayList<>();

    public TestManager(Carbon main) {
        this.main   = main;
        this.logger = main.getLogger("CarbonTest");
    }

    public TestManager addTest(CompiledTest test) {
        tests.add(test);
        return this;
    }

    public TestManager addToCompile(Class<?> klass) {
        classesToCompile.add(klass);
        return this;
    }

    private static final Class<?>[] requiredParamTypes = new Class[] {
            Carbon.class,
            TestManager.class,
            BukkitLogger.class
    };

    public TestManager compileClass(Class<?> klass) {
        logger.stage("Compile: " + klass.getSimpleName());
        try {
            for (Method method : klass.getDeclaredMethods()) {
                CarbonTest testA;
                if ((testA = method.getAnnotation(CarbonTest.class)) == null)
                    continue;
                if (!Arrays.equals(method.getParameterTypes(), requiredParamTypes)) {
                    logger.warn("Method " + method.toGenericString() + " is annotated as @CarbonTest but has wrong parameter types.");
                    continue;
                }
                String name = testA.name();
                if ("<get>".equals(name))
                    name = klass.getSimpleName() + "/" + method.getName();
                String id = testA.id();
                if ("<get>".equals(id))
                    id = klass.getSimpleName() + ":" + method.getName();
                CompiledTest test = new CompiledTest(Identifier.of(id), name, method) {
                    @Override
                    public void execute(Carbon main, TestManager manager, BukkitLogger logger)
                            throws Exception {
                        method.invoke(null, main, manager, logger);
                    }
                };
                addTest(test);
            }
        } catch (Exception e) {
            logger.err("Failed to compile " + klass + " for testing");
            e.printStackTrace();
        }

        return this;
    }

    public TestManager compileAll() {
        for (Class<?> klass : classesToCompile)
            compileClass(klass);
        return this;
    }

    public TestManager runAll() {
        for (CompiledTest test : tests) {
            logger.stage("Run: " + test.getName());
            try {
                test.execute(
                        main,
                        this,
                        logger
                );
            } catch (Exception e) {
                logger.err("Test failed with exception: " + test.getId() + " ( " + test.getName() + " ) from " + test.getSource());
                e.printStackTrace();
                return this;
            }
        }

        return this;
    }

}
