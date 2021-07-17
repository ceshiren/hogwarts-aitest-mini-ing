package com.hogwartsmini.demo.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * @Author tlibn
 * @Date 2020/7/17 16:42
 **/
@Component
@Slf4j
public class DemoInterceptor implements HandlerInterceptor {

    @Autowired
    private TokenDb tokenDb;

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        log.info("=== preHandle ====");
        log.info("=== request.getRequestURI() ====" + request.getRequestURI());

        //1、从请求的Header获取客户端附加token

        String token = request.getHeader("token");
        String uri = request.getRequestURI();

        if("/user/login".equals(uri) ||
                "/user/register".equals(uri)){
            return true;
        }

        //2、如果请求中无token，响应码设401，抛出业务异常：客户端未传token

        if(Objects.isNull(token) || token==""){
            ServiceException.throwEx("客户端未传token");
        }

        //3、从tokenDb中根据token查询TokenDto

        //如果为空，则响应码设401，抛出业务异常：
        //用户未登录
        boolean loginFlag = tokenDb.isLogin(token);

        if (!loginFlag){
            ServiceException.throwEx("用户未登录");
        }

        //否则，允许请求通过

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
