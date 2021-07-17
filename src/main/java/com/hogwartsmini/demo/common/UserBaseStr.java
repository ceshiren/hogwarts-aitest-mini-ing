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
    //任务类型 默认1
    public static final Integer Task_Type_One = 1;
    //任务状态 1 新建
    public static final Integer STATUS_ONE = 1;
    //任务状态 2 执行中
    public static final Integer STATUS_TWO = 2;
    //任务状态 3 执行完成
    public static final Integer STATUS_THREE = 3;

    public static final Integer CASE_TYPE_ONE = 1;
    public static final Integer CASE_TYPE_TWO = 2;
}
