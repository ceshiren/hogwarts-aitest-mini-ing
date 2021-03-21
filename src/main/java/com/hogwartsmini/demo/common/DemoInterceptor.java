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


        String requestURI = request.getRequestURI();
        String requestURL = request.getRequestURL().toString();
        log.info("=== preHandle ====");
        log.info("=== request.getRequestURI() ====" + request.getRequestURI());
        log.info("=== request.requestURL() ====" + requestURL);


        if(requestURI.equalsIgnoreCase("/hogwartsUser/login")
               || requestURI.equalsIgnoreCase("/hogwartsUser/register") ){
            return true;
        }


        //1、从请求的Header获取客户端附加token

        String tokenStr = request.getHeader(UserBaseStr.LOGIN_TOKEN);



        //2、如果请求中无token，响应码设401，抛出业务异常：客户端未传token

        if(Objects.isNull(tokenStr)){
            response.setStatus(401);
            ServiceException.throwEx("客户端未传token");

        }
        //3、从tokenDb中根据token查询TokenDto

        if(Objects.isNull(tokenDb.getUserInfo(tokenStr))){
            response.setStatus(401);
            ServiceException.throwEx("用户未登录");
            return false;
        }


        //如果为空，则响应码设401，抛出业务异常：用户未登录
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
