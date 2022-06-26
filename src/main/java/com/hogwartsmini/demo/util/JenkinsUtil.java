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

    public static void  build(String jobName, String userId, String remark,String testCommand) throws IOException, URISyntaxException {

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

    public static ResultDto<HogwartsTestUser> build2(OperateJenkinsJobDto operateJenkinsJobDto) throws IOException, URISyntaxException {

        //获取参数
        String jenkinsUrl = operateJenkinsJobDto.getJenkinsUrl();
        String jenkinsUserName = operateJenkinsJobDto.getJenkinsUserName();
        String jenkinsPassword = operateJenkinsJobDto.getJenkinsPassword();
        String token = operateJenkinsJobDto.getToken();
        HogwartsTestUser hogwartsTestUser = operateJenkinsJobDto.getHogwartsTestUser();
        Map<String, String> params = operateJenkinsJobDto.getParams();
        if(StringUtils.isEmpty(token)){
            return ResultDto.fail("token为空");
        }
        //获取Jenkins job模板xml数据
        String jobConfigXml = getJobXml("JenkinsConfigDir/hogwarts_jenkins_test_start.xml");

        //拼装Jenkins job名称
        String jobName = "hogwarts_" + token;

        //创建Jenkins客户端
        JenkinsHttpClient jenkinsHttpClient = new JenkinsHttpClient(new URI(jenkinsUrl),jenkinsUserName,jenkinsPassword);
        JenkinsServer jenkinsServer = new JenkinsServer(jenkinsHttpClient);

        //根据用户表中的字段判断是创建job还是更新job，为空时创建job并更新用户表，不为空时则更job
        String startTestJobName = hogwartsTestUser.getStartTestJobName();
        if(StringUtils.isEmpty(startTestJobName)){
            //创建job
            jenkinsServer.createJob(jobName,jobConfigXml,true);
            //将job名称赋值给用户对象
            hogwartsTestUser.setStartTestJobName(jobName);
        }else {
            //更新job
            jenkinsServer.updateJob(jobName,jobConfigXml,true);
        }

        //获取当前Jenkins服务器上所有的job
        Map<String, Job> jobMap = jenkinsServer.getJobs();
        //根据当前的job名称查询出当前job
        Job job = jobMap.get(jobName);
        //构建当前job
        job.build(params, true);
        //返回用户信息
        return ResultDto.success("成功", hogwartsTestUser);

    }

    public static String getJobXml(String path) throws IOException {

        ClassPathResource classPathResource = new ClassPathResource(path);
        InputStream inputStream = classPathResource.getInputStream();

        String jobConfigXml = FileUtil.getText(inputStream);

        return jobConfigXml;

    }

    /**
     *  拼装更新任务状态的url
     * @param requestInfoDto
     * @param hogwartsTestTask
     * @return
     */
    public static StringBuilder makeUpdateTaskStatusUrl(RequestInfoDto requestInfoDto, HogwartsTestTask hogwartsTestTask) {

        StringBuilder updateTaskStatusUrl = new StringBuilder();

        updateTaskStatusUrl.append("curl -X PUT ");
        updateTaskStatusUrl.append("\"" + requestInfoDto.getBaseUrl() +"task/status\" ");
        updateTaskStatusUrl.append("-H \"Content-Type:application/json\" ");
        updateTaskStatusUrl.append("-H \"token:"+requestInfoDto.getToken()+"\" ");
        updateTaskStatusUrl.append("-d ");
        JSONObject json = new JSONObject();
        json.put("taskId",hogwartsTestTask.getId());
        json.put("buildUrl","${BUILD_URL}");
        json.put("status", Constants.STATUS_THREE);
        updateTaskStatusUrl.append("'"+json.toJSONString()+"'");

        return updateTaskStatusUrl;

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
