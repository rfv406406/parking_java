package com.sideproject.parking_java.tool;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class DataInforGetting {
    public static Class<?> className;
    public static Method[] methods;
    public static Field[] fields;

    public static void getData(Object obj) {
        className = obj.getClass();
        methods = className.getDeclaredMethods();
        fields = className.getDeclaredFields();

        System.out.println("類名: "+className.getName());

        printMethods(methods);
        printFields(fields);
    }

    public static void printMethods(Object obj) {
        System.out.println("方法名: ");
        for (Method m:methods) {
            System.out.println(m);
        }
    }

    public static void printFields(Object obj) {
        System.out.println("屬性名: ");
        for (Field f:fields) {
            System.out.println(f);
        }
    }
}
