package com.ak47.plugins.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
public class HtmlAop {
    @Value("${web.plugin.view_path}")
    private String viewPath;
    @Value("${web.plugin.view_variable}")
    private String contentVariable;
    @Around(value="execution(* com.ak47.plugins.controller.PluginController.*(..))")
    public Object around(ProceedingJoinPoint joinPoint) {
        try {
            Object result = joinPoint.proceed();
            if(result instanceof String){
                String value = (String)result;
                if(value.matches(".*/.*")){
                    HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
                    request.setAttribute(contentVariable,value);
                    return viewPath;
                }
            }
            if(result instanceof ModelAndView){
                ModelAndView value = (ModelAndView)result;
                String viewName = value.getViewName();
                value.setViewName(viewPath);
                value.addObject(contentVariable,viewName);
                return value;
            }
            return result;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return null;
    }
}
