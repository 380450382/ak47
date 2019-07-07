package com.ak47.plugins.aop;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.AfterReturningAdvice;
import org.springframework.aop.MethodBeforeAdvice;

import java.lang.reflect.Method;

public class ImportAop implements MethodBeforeAdvice,AfterReturningAdvice, MethodInterceptor {
    @Override
    public void before(Method method, Object[] args, Object target) throws Throwable {
        System.out.println(method.getName() + " start:" + System.currentTimeMillis());
    }

    @Override
    public void afterReturning(Object returnValue, Method method, Object[] args, Object target) throws Throwable {
        System.out.println(method.getName() + " end:" + System.currentTimeMillis());
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        System.out.println("调用" + invocation.getMethod().getName() + "之前成功");
        Object result=invocation.proceed();
        System.out.println("调用" + invocation.getMethod().getName() + "之后");
        return result;
    }
}
