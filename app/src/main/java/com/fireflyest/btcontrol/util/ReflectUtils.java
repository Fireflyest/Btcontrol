package com.fireflyest.btcontrol.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 反射
 * @author Fireflyest
 */
public class ReflectUtils {

    private ReflectUtils(){
    }

    /**
     * 反射执行Get
     * @param obj 反射对象
     * @param field Get变量
     * @return 执行得到的值
     */
    public static Object invokeGet(Object obj, String field){
        try {
            String name = field.substring(0,1).toUpperCase() + field.substring(1).toLowerCase();
            Method method = obj.getClass().getMethod("get" + name);
            return method.invoke(obj);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 反射执行Is
     * @param obj 反射对象
     * @param field Is变量
     * @return 执行得到的值
     */
    public static Boolean invokeIs(Object obj, String field){
        try {
            String name = field.substring(0,1).toUpperCase() + field.substring(1).toLowerCase();
            Method method = obj.getClass().getMethod("is" + name);
            if(method.invoke(obj) == null)return false;
            return (Boolean)method.invoke(obj);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 反射执行Set
     * @param obj 反射对象
     * @param field Set变量
     * @param value 设置的值
     */
    public static void invokeSet(Object obj, String field, Object value){
        String name = field.substring(0,1).toUpperCase() + field.substring(1).toLowerCase();
        try {
            Method method = obj.getClass().getMethod("set" + name, value.getClass());
            method.invoke(obj, value);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }



}
