package com.hogwartsmini.demo.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
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

        String requestUri =  request.getRequestURI();

        log.info("=== request.getRequestURI() ====" + requestUri);

        //放开不需要登录校验的接口
        boolean passFlag = false;
        if(requestUri.contains("/hogwartsUser/login")
            || requestUri.contains("/hogwartsUser/register")){
            passFlag = true;
        }

        if(passFlag){
            return true;
        }

        //1、从请求的Header获取客户端附加token
        String tokenStr = request.getHeader(UserBaseStr.LOGIN_TOKEN);

        //2、如果请求中无token，响应码设401，抛出业务异常：客户端未传token

        if(StringUtils.isEmpty(tokenStr)){
            response.setStatus(401);
            ServiceException.throwEx("客户端未传token");
        }
        //3、从tokenDb中根据token查询TokenDto
        //如果为空，则响应码设401，抛出业务异常：用户未登录
        if(Objects.isNull(tokenDb.getUserInfo(tokenStr))){
            response.setStatus(401);
            ServiceException.throwEx("用户未登录");
        }

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
