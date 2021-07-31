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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

/**
 * @Author tlibn
 * @Date 2020/7/17 11:03
 **/
@Service
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

        StringBuilder testCommand = new StringBuilder();

        AddHogwartsTestTaskDto testTask = testTaskDto.getTestTask();
        List<Integer> caseIdList  = testTaskDto.getCaseIdList();

        //再次校验testTask、caseIdList是否为空

        if(Objects.isNull(caseIdList) || caseIdList.size()==0){
            return ResultDto.fail("测试用例数据不能为空");
        }

        Integer createUserId = testTask.getCreateUserId();
        Integer testJenkinsId = testTask.getTestJenkinsId();

        //校验testJenkinsId是否为空

        if(Objects.isNull(testJenkinsId)){
            return ResultDto.fail("默认Jenkins未设置");
        }

        //根据用户默认JenkinsId查询Jenkins信息并做非空校验

        HogwartsTestJenkins queryHogwartsTestJenkins = new HogwartsTestJenkins();
        queryHogwartsTestJenkins.setId(testJenkinsId);
        queryHogwartsTestJenkins.setCreateUserId(createUserId);

        HogwartsTestJenkins resultHogwartsTestJenkins = hogwartsTestJenkinsMapper.selectOne(queryHogwartsTestJenkins);

        if(Objects.isNull(resultHogwartsTestJenkins)){
            return ResultDto.fail("默认Jenkins未设置");
        }

        //根据用户选择的测试用例id查询测试用例信息

        String idListStr = StrUtil.list2IdsStr(caseIdList);

        List<HogwartsTestCase> hogwartsTestCaseList = hogwartsTestCaseMapper.selectByIds(idListStr);

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

        //测试任务详情数据存库

        List<HogwartsTestTaskCaseRel> hogwartsTestTaskCaseRelList = new ArrayList<>();

        for (Integer caseId:caseIdList) {
            HogwartsTestTaskCaseRel hogwartsTestTaskCaseRel = new HogwartsTestTaskCaseRel();
            hogwartsTestTaskCaseRel.setCaseId(caseId);
            hogwartsTestTaskCaseRel.setTaskId(hogwartsTestTask.getId());
            hogwartsTestTaskCaseRel.setCreateTime(new Date());
            hogwartsTestTaskCaseRel.setCreateUserId(createUserId);
            hogwartsTestTaskCaseRel.setUpdateTime(new Date());
            hogwartsTestTaskCaseRelList.add(hogwartsTestTaskCaseRel);

        }

        hogwartsTestTaskCaseRelMapper.insertList(hogwartsTestTaskCaseRelList);

        //如果想中断数据落库，此处判断业务中断情况时，应直接抛出异常，以阻止事务的提交
        /*if(true){
           throw new NullPointerException();
        }*/

        return ResultDto.success("成功", hogwartsTestTask);
    }

    /**
     *  生成测试命令
     * @param testCommandStr
     * @param resultHogwartsTestJenkins
     * @param hogwartsTestCaseList
     */
    private void makeTestCommand(StringBuilder testCommandStr, HogwartsTestJenkins resultHogwartsTestJenkins, List<HogwartsTestCase> hogwartsTestCaseList) {
        //校验Jenkins是否为空

        if(Objects.isNull(resultHogwartsTestJenkins)){
            ServiceException.throwEx("Jenkins为空");
        }
        //获取commandRunCaseType、testCommand
        String testCommand = resultHogwartsTestJenkins.getTestCommand();
        Integer commandRunCaseType = resultHogwartsTestJenkins.getCommandRunCaseType();

        if(StringUtils.isEmpty(testCommand)){
            ServiceException.throwEx("Jenkins的测试命令为空");
        }

        //用例类型为空，则设置默认类型为文本
        if(StringUtils.isEmpty(resultHogwartsTestJenkins.getCommandRunCaseType())){
            commandRunCaseType = 1;
        }

        testCommandStr.append("pwd").append("\n");
        //文本类型处理方式

        if(commandRunCaseType==1){

            for (int i = 0; i < hogwartsTestCaseList.size(); i++) {
                HogwartsTestCase hogwartsTestCase = hogwartsTestCaseList.get(i);
                //拼装命令前缀
                testCommandStr.append(testCommand)
                        .append(" ")
                        //拼装测试数据
                        .append(hogwartsTestCase.getCaseData())
                        .append("\n");
            }

        }

        //文件类型
        if(commandRunCaseType==2){

            //校验测试用例后缀名
            String commandRunCaseSuffix = resultHogwartsTestJenkins.getCommandRunCaseSuffix();

            for (int i = 0; i < hogwartsTestCaseList.size(); i++) {


                HogwartsTestCase hogwartsTestCase = hogwartsTestCaseList.get(i);

                //拼装curl命令-下载测试用例数据
                makeCurlCommand(testCommandStr, hogwartsTestCase, commandRunCaseSuffix);

                //拼装测试命令
                testCommandStr.append(testCommand)
                        .append(" ")
                        //拼装测试数据
                        .append(hogwartsTestCase.getCaseName())
                        .append(".")
                        .append(commandRunCaseSuffix)
                        .append(" || true")
                        .append("\n");
            }

        }

        testCommandStr.append("\n");
    }

    /**
     *  组装curl命令，用于获取测试数据并生成文件
     * @param testCommand
     * @param hogwartsTestCase
     * @param commandRunCaseSuffix
     */
    private void makeCurlCommand(StringBuilder testCommand, HogwartsTestCase hogwartsTestCase, String commandRunCaseSuffix) {

        testCommand.append("curl ")
                .append(" -o ");

        String caseName = hogwartsTestCase.getCaseName();

        testCommand.append(caseName)
                .append(".")
                .append(commandRunCaseSuffix)
                .append(" ${aitestBaseUrl}/testCase/data")
                .append(hogwartsTestCase.getId())
                .append(" -H \\\"token: ${token}\\\"")
                .append(" || true")
                .append("\n");
        ;

    }

    /**
     *  生成Junit测试命令
     * @param testCommand
     * @param resultHogwartsTestJenkins
     * @param hogwartsTestCaseList
     */
    private void makeJunitTestCommand(StringBuilder testCommand, HogwartsTestJenkins resultHogwartsTestJenkins, List<HogwartsTestCase> hogwartsTestCaseList) {
        //暂时给默认值 TODO
        //mvn test -test=包名 类名 方法名的组合
        testCommand.append("默认命令");

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
            return  ResultDto.fail("默认Jenkins未设置");
        }

        Integer userId = tokenDto.getUserId();

        HogwartsTestUser queryUser = new HogwartsTestUser();
        queryUser.setId(userId);

        HogwartsTestUser resultUser = hogwartsTestUserMapper.selectOne(queryUser);

        HogwartsTestJenkins queryHogwartsTestJenkins = new HogwartsTestJenkins();

        queryHogwartsTestJenkins.setId(defaultJenkinsId);
        queryHogwartsTestJenkins.setCreateUserId(userId);

        HogwartsTestJenkins resultHogwartsTestJenkins  = hogwartsTestJenkinsMapper.selectOne(queryHogwartsTestJenkins);

        if(Objects.isNull(resultHogwartsTestJenkins)){
            return  ResultDto.fail("默认Jenkins未查到");
        }

        //查询测试任务

        HogwartsTestTask queryHogwartsTestTask = new HogwartsTestTask();

        queryHogwartsTestTask.setCreateUserId(hogwartsTestTask.getCreateUserId());
        queryHogwartsTestTask.setStatus(UserBaseStr.STATUS_ONE);
        queryHogwartsTestTask.setId(hogwartsTestTask.getId());

        HogwartsTestTask resultHogwartsTestTask = hogwartsTestTaskMapper.selectOne(queryHogwartsTestTask);

        if(Objects.isNull(resultHogwartsTestTask)){
            return ResultDto.fail("任务未查到，请确认");
        }

        //获取测试命令并更新任务状态为执行中

        String testCommand = resultHogwartsTestTask.getTestCommand();
        resultHogwartsTestTask.setStatus(UserBaseStr.STATUS_TWO);

        hogwartsTestTaskMapper.updateByPrimaryKeySelective(resultHogwartsTestTask);

        //获取更新任务状态的回调地址updateStatusUrl

        StringBuilder updateTaskStatusStr = JenkinsUtil
                .getUpdateTaskStatusUrl(requestInfoDto, resultHogwartsTestTask);


        //组装Jenkins构建参数

        Map map = new HashMap();

        map.put("aitestBaseUrl",requestInfoDto.getBaseUrl());
        map.put("token",requestInfoDto.getToken());
        map.put("testCommand",testCommand);
        map.put("updateStatusData",updateTaskStatusStr.toString());

        //调用Jenkins

        OperateJenkinsJobDto operateJenkinsJobDto = new OperateJenkinsJobDto();

        operateJenkinsJobDto.setTokenDto(tokenDto);
        operateJenkinsJobDto.setHogwartsTestJenkins(resultHogwartsTestJenkins);
        operateJenkinsJobDto.setParams(map);
        operateJenkinsJobDto.setHogwartsTestUser(resultUser);

        ResultDto<HogwartsTestUser> resultDto = JenkinsUtil.build2(operateJenkinsJobDto);

        if(resultDto.getResultCode()==0){
            ServiceException.throwEx(resultDto.getMessage());
        }

        HogwartsTestUser resultHogwartsTestUser = resultDto.getData();

        hogwartsTestUserMapper.updateByPrimaryKeySelective(resultHogwartsTestUser);

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
