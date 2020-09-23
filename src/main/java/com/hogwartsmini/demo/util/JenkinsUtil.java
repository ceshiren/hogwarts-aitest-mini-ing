package com.hogwartsmini.demo.util;

import com.alibaba.fastjson.JSONObject;
import com.hogwartsmini.demo.common.ResultDto;
import com.hogwartsmini.demo.common.TokenDto;
import com.hogwartsmini.demo.common.UserBaseStr;
import com.hogwartsmini.demo.dto.RequestInfoDto;
import com.hogwartsmini.demo.dto.jenkins.OperateJenkinsJobDto;
import com.hogwartsmini.demo.entity.HogwartsTestJenkins;
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

    /**
     *  录播课中的演示代码 - 保留
     * @param jobName
     * @param userId
     * @param remark
     * @param testCommand
     * @throws IOException
     * @throws URISyntaxException
     */
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

    /**
     *  直播课中的演示代码
     * @param operateJenkinsJobDto
     * @throws IOException
     * @throws URISyntaxException
     */
    public static ResultDto<HogwartsTestUser> build(OperateJenkinsJobDto operateJenkinsJobDto) throws IOException, URISyntaxException {

        //获取构建Jenkins的相关参数
        TokenDto tokenDto = operateJenkinsJobDto.getTokenDto();
        HogwartsTestJenkins hogwartsTestJenkins = operateJenkinsJobDto.getHogwartsTestJenkins();
        HogwartsTestUser hogwartsTestUser = operateJenkinsJobDto.getHogwartsTestUser();
        Map<String, String> params = operateJenkinsJobDto.getParams();

        //参数校验 -非空....

        //获取Jenkins的job的配置文件
        ClassPathResource classPathResource = new ClassPathResource("JenkinsConfigDir/hogwarts_jenkins_test_start.xml");
        InputStream inputStream = classPathResource.getInputStream();
        String jobConfigXml = FileUtil.getText(inputStream);

        //参数校验 -非空....
        //获取数据库中Jenkins的信息
        String baseUrl = hogwartsTestJenkins.getUrl();
        String userName = hogwartsTestJenkins.getUserName();
        String password = hogwartsTestJenkins.getPassword();

        //String jobName = "test19";

        //获取Jenkins客户端对象
        JenkinsHttpClient jenkinsHttpClient = new JenkinsHttpClient(new URI(baseUrl),userName,password);
        String jenkinsVersion = jenkinsHttpClient.getJenkinsVersion();
        System.out.println("jenkinsVersion== "+ jenkinsVersion);

        //获取JenkinsServer
        JenkinsServer jenkinsServer = new JenkinsServer(jenkinsHttpClient);

        //拼接Job名称
        String jobName = "hogwarts_test_mini_start_test_"+hogwartsTestUser.getId();

        System.out.println("jobName== "+jobName);
        //判断执行测试job名称是否为空，不为空时表示已经创建job
        if(StringUtils.isEmpty(hogwartsTestUser.getStartTestJobName())){
            jenkinsServer.createJob(jobName,jobConfigXml,true);
            hogwartsTestUser.setStartTestJobName(jobName);
        }else {
            jenkinsServer.updateJob(jobName,jobConfigXml,true);
        }

        Map<String, Job> jobMap = jenkinsServer.getJobs();

        //根据job名称获取Jenkins上的job信息
        Job job = jobMap.get(jobName);


        //为构建参数赋值
        Map<String,String> map = params;

        //实际执行测试时使用的配置
        /*Map<String,String> map = new HashMap<>();
        map.put("aitestBaseUrl",userId);
        map.put("token",remark);
        map.put("testCommand",testCommand);
        map.put("updateStatusData","pwd");*/

        //开始有参构建
        job.build(map,true);

        return ResultDto.success("成功",hogwartsTestUser);

    }


    public static StringBuilder getUpdateTaskStatusUrl(RequestInfoDto requestInfoDto, HogwartsTestTask hogwartsTestTask) {

        StringBuilder updateStatusUrl = new StringBuilder();

        updateStatusUrl.append("curl -X PUT ");
        updateStatusUrl.append("\""+requestInfoDto.getBaseUrl() + "/task/status \" ");
        updateStatusUrl.append("-H \"Content-Type: application/json \" ");
        updateStatusUrl.append("-H \"token: "+requestInfoDto.getToken()+"\" ");
        updateStatusUrl.append("-d ");
        JSONObject json = new JSONObject();

        //确认修改状态的任务
        json.put("taskId",hogwartsTestTask.getId());
        //
        json.put("status", UserBaseStr.STATUS_THREE);
        //Jenkins的构建地址
        json.put("buildUrl","${BUILD_URL}");

        updateStatusUrl.append("'"+json.toJSONString()+"'");

        return updateStatusUrl;
    }

}
