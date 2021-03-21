package com.hogwartsmini.demo.util;

import com.hogwartsmini.demo.entity.HogwartsTestJenkins;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

/**
 * Jenkins工具类
 * @Author tlibn
 * @Date 2019/8/28 8:48
 **/

@Slf4j
public class ReportUtil2 {

    public static void main(String[] args) {

        String buildUrl = "http://stuq.ceshiren.com:8080/job/hogwarts_test_mini_start_test_12/23/";

        HogwartsTestJenkins hogwartsTestJenkins = new HogwartsTestJenkins();

        hogwartsTestJenkins.setUrl("http://stuq.ceshiren.com:8080/");
        hogwartsTestJenkins.setUserName("hogwarts");
        hogwartsTestJenkins.setPassword("hogwarts123");

        String url = getAllureReportUrl(buildUrl,hogwartsTestJenkins,true);
        System.out.println("url== "+url);

    }

    /**
     *  获取allure报告地址
     * @param buildUrl 构建地址
     * @param hogwartsTestJenkins Jenkins记录表
     * @param autoLoginJenkinsFlag 是否自动登录Jenkins
     * @return
     */

    public static String getAllureReportUrl(String buildUrl, HogwartsTestJenkins hogwartsTestJenkins
            , boolean autoLoginJenkinsFlag){

        if(StringUtils.isEmpty(buildUrl) || !buildUrl.contains("/job")){
            return buildUrl;
        }
        String allureReportUrl = buildUrl;

        if(autoLoginJenkinsFlag){
            allureReportUrl = getAllureReportUrlAndLogin(buildUrl, hogwartsTestJenkins);
        }
        return allureReportUrl + "allure/";
    }

    /**
     *  获取可以自动登录Jenkins的allure报告地址
     * @param buildUrl
     * @param hogwartsTestJenkins
     * @return
     */
    private static String getAllureReportUrlAndLogin(String buildUrl, HogwartsTestJenkins hogwartsTestJenkins) {
        String allureReportUrl;
        String allureReportBaseUrl = hogwartsTestJenkins.getUrl()
                + "j_acegi_security_check?j_username="+hogwartsTestJenkins.getUserName()
                +"&j_password="+hogwartsTestJenkins.getPassword()
                +"&Submit=登录&remember_me=on"
                +"&from=";
        allureReportUrl = allureReportBaseUrl + buildUrl.substring(buildUrl.indexOf("/job"));
        return allureReportUrl;
    }

}
