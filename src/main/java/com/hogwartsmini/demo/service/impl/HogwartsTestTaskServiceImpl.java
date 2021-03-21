package com.hogwartsmini.demo.service.impl;

import com.hogwartsmini.demo.common.*;
import com.hogwartsmini.demo.dao.*;
import com.hogwartsmini.demo.dto.AllureReportDto;
import com.hogwartsmini.demo.dto.RequestInfoDto;
import com.hogwartsmini.demo.dto.jenkins.OperateJenkinsJobDto;
import com.hogwartsmini.demo.dto.jenkins.QueryHogwartsTestJenkinsListDto;
import com.hogwartsmini.demo.dto.task.AddHogwartsTestTaskDto;
import com.hogwartsmini.demo.dto.task.TestTaskDto;
import com.hogwartsmini.demo.entity.*;
import com.hogwartsmini.demo.service.HogwartsTestJenkinsService;
import com.hogwartsmini.demo.service.HogwartsTestTaskService;
import com.hogwartsmini.demo.util.JenkinsUtil;
import com.hogwartsmini.demo.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.security.sasl.SaslException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

/**
 * @Author tlibn
 * @Date 2020/7/17 11:03
 **/
@Service
@Slf4j
public class HogwartsTestTaskServiceImpl implements HogwartsTestTaskService {

    @Autowired
    private HogwartsTestJenkinsMapper hogwartsTestJenkinsMapper;

    @Autowired
    private HogwartsTestTaskMapper hogwartsTestTaskMapper;

    @Autowired
    private HogwartsTestCaseMapper hogwartsTestCaseMapper;

    @Autowired
    private HogwartsTestTaskCaseRelMapper hogwartsTestTaskCaseRelMapper;

    @Autowired
    private HogwartsTestUserMapper hogwartsTestUserMapper;


    @Autowired
    private TokenDb tokenDb;


    /**
     * 新增测试任务信息
     *
     * @param testTaskDto
     * @param taskType
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultDto<HogwartsTestTask> save(TestTaskDto testTaskDto, Integer taskType) {

        //要拼接的测试命令
        StringBuilder testCommand = new StringBuilder();

        AddHogwartsTestTaskDto testTask = testTaskDto.getTestTask();
        List<Integer> caseIdList  = testTaskDto.getCaseIdList();

        //再次校验testTask、caseIdList是否为空
        Integer createUserId = testTask.getCreateUserId();
        Integer testJenkinsId = testTask.getTestJenkinsId();

        //校验testJenkinsId是否为空
        if(Objects.isNull(testJenkinsId)){
            return ResultDto.fail("用户默认JenkinsId为空");
        }

        //根据用户默认JenkinsId查询Jenkins信息并做非空校验
        HogwartsTestJenkins queryHogwartsTestJenkins = new HogwartsTestJenkins();
        queryHogwartsTestJenkins.setCreateUserId(createUserId);
        queryHogwartsTestJenkins.setId(testJenkinsId);
        HogwartsTestJenkins resultHogwartsTestJenkins = hogwartsTestJenkinsMapper.selectOne(queryHogwartsTestJenkins);
        if(Objects.isNull(resultHogwartsTestJenkins)){
            return ResultDto.fail("用户默认Jenkins信息为空");
        }

        //根据用户选择的测试用例id查询测试用例信息

        String ids = StrUtil.list2IdsStr(caseIdList);

        List<HogwartsTestCase> hogwartsTestCaseList = hogwartsTestCaseMapper.selectByIds(ids);

        //生成测试命令
        makeTestCommand(testCommand, resultHogwartsTestJenkins, hogwartsTestCaseList);

        //测试任务存库
        HogwartsTestTask hogwartsTestTask = new HogwartsTestTask();

        hogwartsTestTask.setName(testTask.getName());
        hogwartsTestTask.setTestJenkinsId(testTask.getTestJenkinsId());
        hogwartsTestTask.setCreateUserId(testTask.getCreateUserId());
        hogwartsTestTask.setRemark(testTask.getRemark());

        hogwartsTestTask.setTaskType(taskType);
        hogwartsTestTask.setTestCommand(testCommand.toString());
        hogwartsTestTask.setCaseCount(caseIdList.size());
        hogwartsTestTask.setStatus(UserBaseStr.STATUS_ONE);
        hogwartsTestTask.setCreateTime(new Date());
        hogwartsTestTask.setUpdateTime(new Date());

        hogwartsTestTaskMapper.insert(hogwartsTestTask);

        //todo 测试任务详情数据存库

        return ResultDto.success("成功", hogwartsTestTask);
    }

    /**
     *  生成测试命令
     * @param testCommand
     * @param resultHogwartsTestJenkins
     * @param hogwartsTestCaseList
     */
    private void makeTestCommand(StringBuilder testCommand, HogwartsTestJenkins resultHogwartsTestJenkins, List<HogwartsTestCase> hogwartsTestCaseList) {

        testCommand.append("pwd");
        testCommand.append("\n");

        String startTestCommand = resultHogwartsTestJenkins.getTestCommand();
        Integer commandRunCaseType = resultHogwartsTestJenkins.getCommandRunCaseType();

        //默认值
        if(Objects.isNull(commandRunCaseType)){
            commandRunCaseType = 1;
        }


        //测试用例为文本类型时
        if(commandRunCaseType==1){

            //如mvn test系列测试工具
            testCommand.append(startTestCommand);

            for (HogwartsTestCase hogwartsTestCase:hogwartsTestCaseList) {
                testCommand.append(hogwartsTestCase.getCaseData());
            }

        }
        //测试用例为文件类型时
        if(commandRunCaseType==2){

            String commandRunCaseSuffix = resultHogwartsTestJenkins.getCommandRunCaseSuffix();

            for (HogwartsTestCase hogwartsTestCase:hogwartsTestCaseList) {
                makeCurlCommand(testCommand, hogwartsTestCase, commandRunCaseSuffix);
                testCommand.append("\n");

                testCommand.append(startTestCommand).append(" ");

                testCommand.append(hogwartsTestCase.getCaseName())
                        .append(".").append(commandRunCaseSuffix);

                testCommand.append(" || true");
                testCommand.append("\n");


            }


        }
        log.info("testCommand==  " + testCommand);



    }

    /**
     *  组装curl命令，用于获取测试数据并生成文件
     * @param testCommand
     * @param hogwartsTestCase
     * @param commandRunCaseSuffix
     */
    private void makeCurlCommand(StringBuilder testCommand
            , HogwartsTestCase hogwartsTestCase
            , String commandRunCaseSuffix) {
        testCommand.append("curl -o ");
        testCommand.append(hogwartsTestCase.getCaseName())
                .append(".")
                .append(commandRunCaseSuffix);
        testCommand.append(" ");
        testCommand.append("${aitestBaseUrl}")
                .append("/testCase/data/").append(hogwartsTestCase.getId());
        testCommand.append(" ");
        testCommand.append("-H \"token: ${token}\"");
        testCommand.append(" || true");

    }

    /**
     * 开始执行测试任务信息
     *
     * @param tokenDto
     * @param requestInfoDto
     * @param hogwartsTestTask
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultDto startTask(TokenDto tokenDto, RequestInfoDto requestInfoDto, HogwartsTestTask hogwartsTestTask) throws IOException, URISyntaxException {
        //参数校验和默认Jenkins是否有效
        Integer defaultJenkinsId = tokenDto.getDefaultJenkinsId();
        if(Objects.isNull(defaultJenkinsId)){
            return ResultDto.fail("默认JenkinsId为空");
        }
        HogwartsTestJenkins queryHogwartsTestJenkins = new HogwartsTestJenkins();
        queryHogwartsTestJenkins.setCreateUserId(tokenDto.getUserId());
        queryHogwartsTestJenkins.setId(defaultJenkinsId);

        HogwartsTestJenkins resultHogwartsTestJenkins = hogwartsTestJenkinsMapper.selectOne(queryHogwartsTestJenkins);

        if(Objects.isNull(resultHogwartsTestJenkins)){
            return ResultDto.fail("默认Jenkins信息为空");
        }

        //获取用户信息

        //做userId和resultHogwartsTestUser非空校验
        HogwartsTestUser queryHogwartsTestUser = new HogwartsTestUser();
        queryHogwartsTestUser.setId(tokenDto.getUserId());
        HogwartsTestUser resultHogwartsTestUser = hogwartsTestUserMapper.selectOne(queryHogwartsTestUser);

        //根据任务Id查询测试任务

        HogwartsTestTask queryHogwartsTestTask = new HogwartsTestTask();
        queryHogwartsTestTask.setId(hogwartsTestTask.getId());
        HogwartsTestTask resultHogwartsTestTask = hogwartsTestTaskMapper.selectOne(queryHogwartsTestTask);

        if(Objects.isNull(resultHogwartsTestTask)){
            return ResultDto.fail("任务信息不存在");
        }
        //获取测试命令并更新任务状态为执行中
        resultHogwartsTestTask.setStatus(UserBaseStr.STATUS_TWO);
        hogwartsTestTaskMapper.updateByPrimaryKeySelective(resultHogwartsTestTask);

        //获取更新任务状态的回调地址updateStatusUrl
        StringBuilder updateStatusUrl = JenkinsUtil.getUpdateTaskStatusUrl(requestInfoDto, resultHogwartsTestTask);
        String updateStatusUrlStr = updateStatusUrl.toString();
        //组装Jenkins构建参数

        String testCommand = resultHogwartsTestTask.getTestCommand();

        Map<String,String> map = new HashMap<>();
        map.put("aitestBaseUrl",requestInfoDto.getBaseUrl());
        map.put("token",requestInfoDto.getToken());
        map.put("testCommand",testCommand);
        map.put("updateStatusData",updateStatusUrlStr);
        // todo 调用Jenkins
        OperateJenkinsJobDto operateJenkinsJobDto = new OperateJenkinsJobDto();

        operateJenkinsJobDto.setHogwartsTestUser(resultHogwartsTestUser);
        operateJenkinsJobDto.setHogwartsTestJenkins(resultHogwartsTestJenkins);
        operateJenkinsJobDto.setTokenDto(tokenDto);
        operateJenkinsJobDto.setParams(map);

        ResultDto<HogwartsTestUser>  hogwartsTestUserResultDto = JenkinsUtil.build2(operateJenkinsJobDto);

        if(hogwartsTestUserResultDto.getResultCode()==1){

            HogwartsTestUser resultHogwartsTestUser2 = hogwartsTestUserResultDto.getData();

            hogwartsTestUserMapper.updateByPrimaryKeySelective(resultHogwartsTestUser2);

        }


        return ResultDto.success("成功",resultHogwartsTestTask);
    }

    /**
     * 修改测试任务状态信息
     *
     * @param hogwartsTestTask
     * @return
     */
    @Override
    public ResultDto<HogwartsTestTask> updateStatus(HogwartsTestTask hogwartsTestTask) {
        return null;
    }

    /**
     * 获取allure报告
     *
     * @param tokenDto
     * @param taskId
     * @return
     */
    @Override
    public ResultDto<AllureReportDto> getAllureReport(TokenDto tokenDto, Integer taskId) {
        return null;
    }
}
