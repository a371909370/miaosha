package com.miaosha;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.junit.Test;

public class AppTest extends TestCase {

    @Test
    public void test(){
        String a = new String("123"),
                b = new String("123");
        System.out.println(a == b);
    }

}
