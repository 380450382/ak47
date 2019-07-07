package com.ak47.plugins.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class ObjectUtils {
    private ObjectUtils(){

    }

    /**
     * 创建T并设置值(obj中的值)返回
     * @param tClass
     * @param obj
     * @param <T>
     */
    public static <T> T setTbyObj (Class<T> tClass, Object obj){
        if(tClass != null && obj != null) {
            try {
                T t = tClass.getDeclaredConstructor().newInstance();
                Method[] methods = tClass.getDeclaredMethods();
                for (Method method : methods) {
                    if (method.getName().startsWith("get")) {
                        Method objMethod = obj.getClass().getDeclaredMethod(method.getName());
                        Object value = objMethod.invoke(obj, null);
                        Method setMethod = tClass.getDeclaredMethod(method.getName().replace("get", "set"), objMethod.getReturnType());
                        setMethod.invoke(t, value);
                    }
                }
                return t;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
