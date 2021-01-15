package com.test.mq.concurrency.classname;


public class ClassForName {

    private static String num = getnums();
    private static Integer nums = 1;

    static {
        System.out.println("执行静态代码块"+nums);

    }
    private static String  getnums(){
        System.out.println("执行了静态方法");
        return "1";
    }

}
