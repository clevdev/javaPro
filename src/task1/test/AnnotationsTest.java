package task1.test;

import task1.annotation.*;

public class AnnotationsTest {

    @BeforeSuite
    public static void beforeSuite() {
        System.out.println("run BeforeSuite");
    }

    @AfterSuite
    public static void afterSuite() {
        System.out.println("run AfterSuite");
    }

    @BeforeTest
    public static void beforeTest() {
        System.out.println("run BeforeTest");
    }

    @AfterTest
    public static void afterTest() {
        System.out.println("run AfterTest");
    }

    @Test(priority = 1)
    public void testMethod1() {
        System.out.println("run Test method 1");
    }

    @Test(priority = 2)
    public void testMethod2() {
        System.out.println("run Test method 2");
    }

    @Test(priority = 3)
    public void testMethod3() {
        System.out.println("run Test method 3");
    }

    @Test(priority = 4)
    public void testMethod4() {
        System.out.println("run Test method 4");
    }

    @Test
    public void testMethod() {
        System.out.println("run Test method with default priority");
    }

    @CsvSource("10,Java,20,true")
    public void testCsvSource(int a,String b, int c, boolean d) {
        System.out.println("Parse @CsvSource: a =" + a + ", b = " + b + ", c = " + c + ", d = " + d);
    }

}
