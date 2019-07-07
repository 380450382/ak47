package com.ak47.plugins.handler;

import java.lang.reflect.Method;

public interface CutPoint {
    boolean handle(String expression, Method method);
    boolean support(String expressionType);
}
