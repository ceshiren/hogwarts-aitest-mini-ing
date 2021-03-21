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
    /*public static void main(String[] args) throws IOException, URISyntaxException {
        build("hogwarts_test_mini_start_test_12","12","token","pwd");
    }*/

    /**
     *  录播课内容
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
     *  直播课内容
     * @param operateJenkinsJobDto
     * @throws IOException
     * @throws URISyntaxException
     */
    public static ResultDto<HogwartsTestUser> build2(OperateJenkinsJobDto operateJenkinsJobDto) throws IOException, URISyntaxException {


        HogwartsTestUser hogwartsTestUser = operateJenkinsJobDto.getHogwartsTestUser();
        HogwartsTestJenkins hogwartsTestJenkins = operateJenkinsJobDto.getHogwartsTestJenkins();
        Map<String, String> params = operateJenkinsJobDto.getParams();
        TokenDto tokenDto = operateJenkinsJobDto.getTokenDto();


        ClassPathResource classPathResource = new ClassPathResource("JenkinsConfigDir/hogwarts_jenkins_test_start.xml");
        InputStream inputStream = classPathResource.getInputStream();

        String jobConfigXml = FileUtil.getText(inputStream);

        String baseUrl = hogwartsTestJenkins.getUrl();
        String userName = hogwartsTestJenkins.getUserName();
        String password = hogwartsTestJenkins.getPassword();

        //String jobName = "test19";

        JenkinsHttpClient jenkinsHttpClient = new JenkinsHttpClient(new URI(baseUrl),userName,password);
        String jenkinsVersion = jenkinsHttpClient.getJenkinsVersion();
        System.out.println("jenkinsVersion== "+ jenkinsVersion);

        JenkinsServer jenkinsServer = new JenkinsServer(jenkinsHttpClient);

        String jobName = "hogwarts_test_mini_start_test_A"+hogwartsTestUser.getId();

        String startTestJobName = hogwartsTestUser.getStartTestJobName();

        if(StringUtils.isEmpty(startTestJobName)){
            hogwartsTestUser.setStartTestJobName(jobName);
            jenkinsServer.createJob(jobName,jobConfigXml,true);
        }else {
            jenkinsServer.updateJob(jobName,jobConfigXml,true);
        }

        Map<String, Job> jobMap = jenkinsServer.getJobs();

        Job job = jobMap.get(jobName);

        job.build(params,true);

        System.out.println("");

        /*Map<String, Job> jobMap = jenkinsServer.getJobs();

        Job job = jobMap.get("");

        job.build(true);*/

        return ResultDto.success("成功",hogwartsTestUser);

    }




    public static StringBuilder getUpdateTaskStatusUrl(RequestInfoDto requestInfoDto
            , HogwartsTestTask hogwartsTestTask) {

        StringBuilder updateStatusUrl = new StringBuilder();

        updateStatusUrl.append("curl -X PUT ");
        updateStatusUrl.append("\""+requestInfoDto.getBaseUrl() + "/hogwartsTask/status \" ");
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
