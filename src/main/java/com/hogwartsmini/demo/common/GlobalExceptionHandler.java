package com.hogwartsmini.demo.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @Author tlibn
 * @Date 2020/7/17 15:29
 **/
@ControllerAdvice
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({ServiceException.class})
    public ResultDto serviceExceptionHandler(ServiceException se){
        return resultFormat(se);
    }

    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    @ExceptionHandler({Exception.class})
    public ResultDto exceptionHandler(Exception e){
        return resultFormat(e);
    }

    @ExceptionHandler({Throwable.class})
    public ResultDto throwableHandler(Throwable t){
        log.error(t.getMessage());
        return ResultDto.fail("系统错误  系统繁忙，请稍后重试");
    }

    public ResultDto resultFormat(Throwable t){
        log.error(t.getMessage());
        String tips = "系统繁忙，请稍后重试";

        if(t instanceof ServiceException){
            return ResultDto.fail("业务异常  " + tips);
        }

        if(t instanceof Exception){
            return ResultDto.fail("非业务异常  " + tips);
        }

        return ResultDto.fail(tips);
    }

}
