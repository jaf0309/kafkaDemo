package com.test.mq.concurrency;


import com.test.mq.concurrency.classname.ClassForName;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ClassForName.class)
//@ActiveProfiles("qa")
public class concurrencyTest {

    @Test
    @Ignore
    public void TestClassForName(){
        try {
            Class.forName("com.test.mq.concurrency.classname.ClassForName");
            System.out.println("---结束---");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Test
    @Ignore
    public void TestClassLoad(){
        try {
            ClassLoader.getSystemClassLoader().loadClass("com.test.mq.concurrency.classname.ClassForName");
            System.out.println("------");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void TestTryCatch(){
        try {
            int i1 = 1 / 0;
        } catch (ArithmeticException e) {
            System.out.println("ArithmeticException");
        }catch (Exception e) {
            System.out.println("Exception");
        }


    }

}
