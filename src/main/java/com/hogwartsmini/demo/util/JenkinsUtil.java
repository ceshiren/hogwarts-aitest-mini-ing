package com.hogwartsmini.demo.util;

import com.alibaba.fastjson.JSONObject;
import com.hogwartsmini.demo.common.Constants;
import com.hogwartsmini.demo.common.ResultDto;
import com.hogwartsmini.demo.dto.OperateJenkinsJobDto;
import com.hogwartsmini.demo.dto.RequestInfoDto;
import com.hogwartsmini.demo.entity.HogwartsTestTask;
import com.hogwartsmini.demo.entity.HogwartsTestUser;
import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.client.JenkinsHttpClient;
import com.offbytwo.jenkins.model.Job;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StringUtils;

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

    public static StringBuilder updateTaskStatusUrl(RequestInfoDto requestInfoDto
            , HogwartsTestTask hogwartsTestTask) {

        StringBuilder updateTaskStatusUrl = new StringBuilder();
        updateTaskStatusUrl.append("curl -X PUT ");
        updateTaskStatusUrl.append("\""+requestInfoDto.getBaseUrl()+"task/status\" ");
        updateTaskStatusUrl.append("-H \"Content-Type: application/json\" ");
        updateTaskStatusUrl.append("-H \"token: "+requestInfoDto.getToken()+"\" ");
        updateTaskStatusUrl.append("-d ");

        JSONObject params = new JSONObject();
        params.put("taskId",hogwartsTestTask.getId());
        params.put("buildUrl","${BUILD_URL}");
        params.put("status", Constants.STATUS_THREE);
        updateTaskStatusUrl.append("'"+params.toJSONString()+"'");

        return updateTaskStatusUrl;
    }

    public static ResultDto<HogwartsTestUser> build2(OperateJenkinsJobDto operateJenkinsJobDto) throws IOException, URISyntaxException {

        ClassPathResource classPathResource = new ClassPathResource("JenkinsConfigDir/hogwarts_jenkins_test_start.xml");
        InputStream inputStream = classPathResource.getInputStream();

        String jobConfigXml = FileUtil.getText(inputStream);

        String baseUrl = operateJenkinsJobDto.getJenkinsUrl();
        String userName = operateJenkinsJobDto.getJenkinsUserName();
        String password = operateJenkinsJobDto.getJenkinsPassword();

        String jobName = "hogwarts_task_" + operateJenkinsJobDto.getToken();

        JenkinsHttpClient jenkinsHttpClient = new JenkinsHttpClient(new URI(baseUrl),userName,password);
        String jenkinsVersion = jenkinsHttpClient.getJenkinsVersion();
        System.out.println("jenkinsVersion== "+ jenkinsVersion);

        JenkinsServer jenkinsServer = new JenkinsServer(jenkinsHttpClient);

        HogwartsTestUser hogwartsTestUser = operateJenkinsJobDto.getHogwartsTestUser();

        String startTestJobName = hogwartsTestUser.getStartTestJobName();

        //如果用户未创建job，则创建job，否则更新job，然后再构建job
        if(StringUtils.isEmpty(startTestJobName)){
            jenkinsServer.createJob(jobName,jobConfigXml,true);
            hogwartsTestUser.setStartTestJobName(jobName);
        }else {
            jenkinsServer.updateJob(jobName,jobConfigXml,true);
        }

        Map<String, Job> jobMap = jenkinsServer.getJobs();

        Job job = jobMap.get(jobName);

        Map<String,String> map = operateJenkinsJobDto.getParams();

        job.build(map,true);

        return ResultDto.success("成功",hogwartsTestUser);
    }


}
