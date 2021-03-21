package com.hogwartsmini.demo.common;

/**
 * @Author tlibn
 * @Date 2020/9/12 17:30
 **/
public class UserBaseStr {

    //获取请求的header中token的key
    public static final String LOGIN_TOKEN = "token";
    //生成密码的key
    public static final String md5Hex_sign = "Hogwarts_aitest-mini";
    //public static final String md5Hex_sign = "HogwartsTest";
    //任务类型 默认1
    public static final Integer Task_Type_One = 1;
    //任务状态 1 新建
    public static final Integer STATUS_ONE = 1;
    //任务状态 2 执行中
    public static final Integer STATUS_TWO = 2;
    //删除标志 未删除
    public static final Integer DEL_FLAG_ONE = 1;
    //删除标志 已删除
    public static final Integer DEL_FLAG_ZERO = 0;

    public static final Integer CASE_TYPE_ONE = 1;
    public static final Integer CASE_TYPE_TWO = 2;
}
