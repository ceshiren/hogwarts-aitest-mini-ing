package com.hogwartsmini.demo.common.jmeter;

import lombok.Data;

/**
 *@Author tlibn
 *@Date 2021/8/25 16:56
 *  jmeter测试结果收集
 **/
@Deprecated
@Data
public class JMeterRequestResult {

    private String url;
    private String responseSize;
    private String responseTime;
    private String responseResult;
    private String consoleResult;
    private String cookie;
    private String requestData;
    private String responseHeader;
    private String requestMethod;
    private String requestHeader;
    private String statusCode;
}
