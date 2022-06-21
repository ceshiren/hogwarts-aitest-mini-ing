package com.hogwartsmini.demo.util;

import com.hogwartsmini.demo.dto.OperateJenkinsJobDto;
import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.client.JenkinsHttpClient;
import com.offbytwo.jenkins.model.Job;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author tlibn
 * @Date 2020/8/11 15:21
 **/
public class JenkinsUtil {

    //实际执行测试时使用的配置
   /* public static void main(String[] args) throws IOException, URISyntaxException {
        build("hogwarts_test_mini_start_test_12","12","token","pwd");
    }*/

    public static void build(String jobName, String userId, String remark,String testCommand) throws IOException, URISyntaxException {

        ClassPathResource classPathResource = new ClassPathResource("JenkinsConfigDir/hogwarts_test_jenkins_show.xml");
        InputStream inputStream = classPathResource.getInputStream();

        String jobConfigXml = FileUtil.getText(inputStream);

        String baseUrl = "http://stuq.ceshiren.com:8080/";
        String userName = "hogwarts";
        String password = "hogwarts123";

        //String jobName = "test19";

        JenkinsHttpClient jenkinsHttpClient = new JenkinsHttpClient(new URI(baseUrl),userName,password);
        String jenkinsVersion = jenkinsHttpClient.getJenkinsVersion();
        System.out.println("jenkinsVersion== "+ jenkinsVersion);

        JenkinsServer jenkinsServer = new JenkinsServer(jenkinsHttpClient);

        jenkinsServer.updateJob(jobName,jobConfigXml,true);

        Map<String, Job> jobMap = jenkinsServer.getJobs();

        Job job = jobMap.get(jobName);

        Map<String,String> map = new HashMap<>();
        map.put("userId",userId);
        map.put("remark",remark);
        map.put("testCommand",testCommand);

        //实际执行测试时使用的配置
        /*Map<String,String> map = new HashMap<>();
        map.put("aitestBaseUrl",userId);
        map.put("token",remark);
        map.put("testCommand",testCommand);
        map.put("updateStatusData","pwd");*/

        job.build(map,true);

        System.out.println("");

        /*Map<String, Job> jobMap = jenkinsServer.getJobs();

        Job job = jobMap.get("");

        job.build(true);*/


    }

    public static String getAllureReportUrl(String buildUrl, OperateJenkinsJobDto operateJenkinsJobDto, boolean autoLoginFlag){

        if(autoLoginFlag){
            return getAllureReportUrl(buildUrl, operateJenkinsJobDto);
        }


        return buildUrl + "allure";
    }

    public static String getAllureReportUrl(String buildUrl, OperateJenkinsJobDto operateJenkinsJobDto){

        String allureReportBaseUrl = operateJenkinsJobDto.getJenkinsUrl()
                + "j_acegi_security_check?j_username=" + operateJenkinsJobDto.getJenkinsUserName()
                + "&j_password=" + operateJenkinsJobDto.getJenkinsPassword()
                + "&Submit=登录&from=";

        String allureReportUrl = allureReportBaseUrl + buildUrl.substring(buildUrl.indexOf("/job"));

        return allureReportUrl + "allure";
    }

    public static void main(String[] args) {
        String buildUrl = "http://stuq.ceshiren.com:8080/job/hogwarts_test_mini_start_test_1/31/";

        OperateJenkinsJobDto operateJenkinsJobDto = new OperateJenkinsJobDto();
        operateJenkinsJobDto.setJenkinsUrl("http://stuq.ceshiren.com:8080/");
        operateJenkinsJobDto.setJenkinsUserName("hogwarts");
        operateJenkinsJobDto.setJenkinsPassword("hogwarts123");

        System.out.println("自动登录url= " + getAllureReportUrl(buildUrl,operateJenkinsJobDto, true));
        System.out.println("========");
        System.out.println("手动登录url= " + getAllureReportUrl(buildUrl,operateJenkinsJobDto, false));
    }

}
