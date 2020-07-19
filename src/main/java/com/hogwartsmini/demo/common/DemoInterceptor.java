package com.hogwartsmini.demo.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author tlibn
 * @Date 2020/7/17 16:42
 **/
@Component
@Slf4j
public class DemoInterceptor implements HandlerInterceptor {

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        log.info("=== preHandle ====");
        log.info("=== request.getRequestURI() ====" + request.getRequestURI());

        return true;
    }

    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {

        log.info("=== postHandle ====");
        log.info("=== request.getRequestURI() ====" + request.getRequestURI());

    }

    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {

        log.info("=== afterCompletion ====");
        log.info("=== request.getRequestURI() ====" + request.getRequestURI());

    }

}
