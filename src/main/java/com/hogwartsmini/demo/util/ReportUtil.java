package com.hogwartsmini.demo.util;

import com.hogwartsmini.demo.dto.OperateJenkinsJobDto;

/**
 *@Author tlibn
 *@Date 2022/3/12 16:20
 **/
public class ReportUtil {

    public static void main(String[] args) {

        String buildUrl = "http://stuq.ceshiren.com:8080/job/hogwarts_test_mini_start_test_1/31/";


        OperateJenkinsJobDto operateJenkinsJobDto = new OperateJenkinsJobDto();

        operateJenkinsJobDto.setJenkinsUrl("http://stuq.ceshiren.com:8080/");
        operateJenkinsJobDto.setJenkinsUserName("hogwarts");
        operateJenkinsJobDto.setJenkinsPassword("hogwarts123");

        StringBuilder result = getAllureReportUrlAndLogin(buildUrl, operateJenkinsJobDto);

        System.out.println("result= " + result);


    }

    public static StringBuilder getAllureReportUrlAndLogin(String buildUrl, OperateJenkinsJobDto operateJenkinsJobDto) {

        String jenkinsUrl = operateJenkinsJobDto.getJenkinsUrl();
        String jenkinsUserName = operateJenkinsJobDto.getJenkinsUserName();
        String jenkinsPassword = operateJenkinsJobDto.getJenkinsPassword();

        buildUrl = buildUrl.replace(jenkinsUrl,"");

        StringBuilder allureReportUrl = new StringBuilder();
        allureReportUrl.append(jenkinsUrl).append("j_acegi_security_check");
        allureReportUrl
                .append("?j_username=").append(jenkinsUserName)
                .append("&j_password=").append(jenkinsPassword)
                .append("&from=").append(buildUrl).append("allure")
                //.append("&remember_me=").append("on")
                //.append("&Submit=").append("登录")
        ;

        return allureReportUrl;

    }

}
