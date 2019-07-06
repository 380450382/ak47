package com.ak47.plugins.handler;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component()
public class ExecutionHandler implements CutPoint {
    @Override
    public boolean support(String expressionType) {
        return expressionType.equals("execution");
    }

    @Override
    public boolean handle(String expression, Method method) {
        //* com.ak47.plugins.service.impl..*.*(..)
        if(StringUtils.isBlank(expression)){
            return false;
        }
        String[] expressions = expression.split(" ");
        String returnType = expressions[0];
        if(!returnType.equals("*")){
            //如果类型不一样
            if(!method.getReturnType().getName().equals(returnType)){
                return false;
            }
        }
        String packageStr = expressions[1];
        String[] packageSub = packageStr.split("\\(")[0].split("\\.\\.");
        for (int i = 0; i < packageSub.length-1; i++) {
            if(!method.getDeclaringClass().getName().matches(".*"+packageSub[i]+".*")){
                return false;
            }
        }
        String packageLastSub = packageSub[packageSub.length-1];
        int index = packageLastSub.lastIndexOf(".");
        packageLastSub = packageLastSub.substring(0,index);
        if(packageLastSub.startsWith("*")){
            packageLastSub = "." + packageLastSub;
        }
        if(!method.getDeclaringClass().getName().matches(".*"+packageLastSub)){
            return false;
        }
        String methodStr = packageSub[packageSub.length-1];
        String methodName = methodStr.substring(index+1);
        if(methodName.equals("*")){
            methodName = "." + methodName;
        }
        if(!method.getName().matches(methodName)){
            return false;
        }
        String[] args = packageStr.split("\\(")[1].split("\\)")[0].split(",");
        Class[] classes = method.getParameterTypes();
        if(args[0].equals("..")){
            if(args.length != 1){
                return false;
            }
        }else {
            if(args.length != classes.length){
                return false;
            }
            for (int i = 0; i < args.length; i++) {
                if(!classes[i].getName().endsWith(args[i])){
                    return false;
                }
            }
        }
        return true;
    }
}
