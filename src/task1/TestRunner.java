package task1;

import task1.annotation.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TestRunner {

    public static void runTests(Class<?> testClass) {

        List<Method> beforeSuiteMethods = new ArrayList<>();
        List<Method> afterSuiteMethods = new ArrayList<>();
        List<Method> beforeTestMethods = new ArrayList<>();
        List<Method> afterTestMethods = new ArrayList<>();
        List<Method> testMethods = new ArrayList<>();
        List<Method> csvSourceMethods = new ArrayList<>();

        Object myTestClass;
        try {
            myTestClass = testClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        Method[] methods = myTestClass.getClass().getDeclaredMethods();

        for (Method method : methods) {
            if (method.isAnnotationPresent(BeforeSuite.class)) {
                if (!beforeSuiteMethods.isEmpty()) {
                    throw new RuntimeException("Only one method with @BeforeSuite is allowed!");
                }
                if (!Modifier.isStatic(method.getModifiers())) {
                    throw new RuntimeException("Only on the static method @BeforeSuite is allowed!");
                }
                beforeSuiteMethods.add(method);
            } else if (method.isAnnotationPresent(AfterSuite.class)) {
                if (!afterSuiteMethods.isEmpty()) {
                    throw new RuntimeException("Only one method with @AfterSuite is allowed!");
                }
                if (!Modifier.isStatic(method.getModifiers())) {
                    throw new RuntimeException("Only on the static method @AfterSuite is allowed!");
                }
                afterSuiteMethods.add(method);
            } else if (method.isAnnotationPresent(Test.class)) {
                testMethods.add(method);
            } else if (method.isAnnotationPresent(BeforeTest.class)) {
                beforeTestMethods.add(method);
            } else if (method.isAnnotationPresent(AfterTest.class)) {
                afterTestMethods.add(method);
            } else if (method.isAnnotationPresent(CsvSource.class)) {
                csvSourceMethods.add(method);
            }
        }

        if (!beforeSuiteMethods.isEmpty()) {
            accept(beforeSuiteMethods.get(0));
        }

        List<Method> sortedTestMethods = testMethods.stream()
                .sorted(Comparator.comparingInt(method -> method.getAnnotation(Test.class).priority()))
                .toList();

        for (Method testMethod : sortedTestMethods) {
            if (!beforeTestMethods.isEmpty()) {
                beforeTestMethods.forEach(TestRunner::accept);
            }
            try {
                testMethod.invoke(myTestClass);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
            if (!afterTestMethods.isEmpty()) {
                afterTestMethods.forEach(TestRunner::accept);
            }
        }

        for (Method method : csvSourceMethods) {
            String csvString = method.getAnnotation(CsvSource.class).value();
            String[] values = csvString.split(",");
            try {
                Object[] params = new Object[values.length];
                for (int i = 0; i < values.length; i++) {
                    params[i] = convertToType(method.getParameterTypes()[i], values[i].trim());
                }
                method.invoke(myTestClass, params);
            } catch (Exception e) {
                throw new RuntimeException("Error parsing @CsvSource value");
            }
        }

        if (!afterSuiteMethods.isEmpty()) {
            accept(afterSuiteMethods.get(0));
        }

    }


    private static void accept(Method method) {
        try {
            method.invoke(null);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private static Object convertToType(Class<?> paramType, String value) {
        if ( paramType == int.class || paramType == Integer.class) {
            return Integer.parseInt(value);
        } else if (paramType == String.class) {
            return value;
        } else if (paramType == boolean.class) {
            return Boolean.parseBoolean(value);
        }
        throw new IllegalArgumentException("Unsupported param type: " + paramType);
    }
}
