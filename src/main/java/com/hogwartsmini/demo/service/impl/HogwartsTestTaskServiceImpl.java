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

import javax.security.sasl.SaslException;
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
        //23,25,29
        List<HogwartsTestCase> hogwartsTestCaseList = hogwartsTestCaseMapper.selectByIds(StrUtil.list2IdsStr(caseIdList));

        //生成测试命令
        makeTestCommand(testCommand,resultHogwartsTestJenkins, hogwartsTestCaseList);

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

        List<HogwartsTestTaskCaseRel> hogwartsTestTaskCaseRelList = new ArrayList<>();
        //测试任务详情数据存库
        for (Integer caseId:caseIdList) {
            HogwartsTestTaskCaseRel hogwartsTestTaskCaseRel = new HogwartsTestTaskCaseRel();
            hogwartsTestTaskCaseRel.setTaskId(hogwartsTestTask.getId());
            hogwartsTestTaskCaseRel.setCaseId(caseId);
            hogwartsTestTaskCaseRel.setCreateUserId(hogwartsTestTask.getCreateUserId());
            hogwartsTestTaskCaseRel.setCreateTime(new Date());
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
     * @param testCommand
     * @param resultHogwartsTestJenkins
     * @param hogwartsTestCaseList
     */
    private void makeTestCommand(StringBuilder testCommand, HogwartsTestJenkins resultHogwartsTestJenkins, List<HogwartsTestCase> hogwartsTestCaseList) {
        /*//暂时给默认值 TODO
        testCommand.append("默认命令");*/


        //校验Jenkins是否为空

        if(Objects.isNull(resultHogwartsTestJenkins)){
            throw new ServiceException("生成命令时Jenkins信息为空");
        }
        if(Objects.isNull(hogwartsTestCaseList) || hogwartsTestCaseList.size()==0){
            throw new ServiceException("生成命令时测试用例列表为空");
        }
        //获取commandRunCaseType、testCommand
        //命令运行的测试用例类型  1 文本 2 文件
        Integer commandRunCaseType = resultHogwartsTestJenkins.getCommandRunCaseType();
        //具体测试工具的运行命令，如mvn test、pytest、hrun
        String testCommandStr = resultHogwartsTestJenkins.getTestCommand();

        //文本类型为空，则设置默认类型为文本
        if(Objects.isNull(commandRunCaseType)){
            commandRunCaseType = 1;
        }

        //文本类型处理方式
        if(UserBaseStr.CASE_TYPE_ONE.equals(commandRunCaseType)){

            //生成文本类型的测试命令，形如
            //mvn test 包1
            //mvn test 包2
            //mvn test 包3
            for (HogwartsTestCase hogwartsTestCase:hogwartsTestCaseList) {
                //拼装命令前缀
                testCommand.append(resultHogwartsTestJenkins.getTestCommand()).append(" ");
                //拼装测试数据
                testCommand.append(hogwartsTestCase.getCaseData());
                testCommand.append("\n");
            }

        }

        //文件类型
        if(UserBaseStr.CASE_TYPE_TWO.equals(commandRunCaseType)){
            //校验测试用例后缀名
            if(StringUtils.isEmpty(resultHogwartsTestJenkins.getCommandRunCaseSuffix())){
                throw new ServiceException("测试用例后缀名 如果case为文件时必填");
            }
            for (HogwartsTestCase hogwartsTestCase:hogwartsTestCaseList) {
                //拼装curl命令-下载测试用例数据
                makeCurlCommand(testCommand,hogwartsTestCase,resultHogwartsTestJenkins.getCommandRunCaseSuffix());
                //拼装测试命令
                testCommand.append("\n");
                testCommand.append(resultHogwartsTestJenkins.getTestCommand()).append(" ");
                testCommand.append(hogwartsTestCase.getCaseName())
                        .append(".")
                .append(resultHogwartsTestJenkins.getCommandRunCaseSuffix());
                testCommand.append(" || true");
                testCommand.append("\n");
            }
        }

        testCommand.append("\n");
    }

    /**
     *  组装curl命令，用于获取测试数据并生成文件
     * @param testCommand
     * @param hogwartsTestCase
     * @param commandRunCaseSuffix
     */
    private void makeCurlCommand(StringBuilder testCommand, HogwartsTestCase hogwartsTestCase, String commandRunCaseSuffix) {
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
        String testCommand = resultHogwartsTestTask.getTestCommand();
        resultHogwartsTestTask.setStatus(UserBaseStr.STATUS_TWO);
        hogwartsTestTaskMapper.updateByPrimaryKeySelective(resultHogwartsTestTask);

        //获取更新任务状态的回调地址updateStatusUrl

        StringBuilder updateStatusUrl = JenkinsUtil.getUpdateTaskStatusUrl(requestInfoDto, resultHogwartsTestTask);

        String updateStatusUrlStr = updateStatusUrl.toString();
        //组装Jenkins构建参数

        Map<String,String> map = new HashMap<>();
        map.put("aitestBaseUrl",requestInfoDto.getBaseUrl());
        map.put("token",requestInfoDto.getToken());
        map.put("testCommand",testCommand);
        map.put("updateStatusData",updateStatusUrlStr);
        //调用Jenkins

        OperateJenkinsJobDto operateJenkinsJobDto = new OperateJenkinsJobDto();
        operateJenkinsJobDto.setTokenDto(tokenDto);
        operateJenkinsJobDto.setHogwartsTestJenkins(resultHogwartsTestJenkins);
        operateJenkinsJobDto.setHogwartsTestUser(resultHogwartsTestUser);
        operateJenkinsJobDto.setParams(map);

        ResultDto<HogwartsTestUser> resultDto = JenkinsUtil.build(operateJenkinsJobDto);

        if(0==resultDto.getResultCode()){
            throw new SaslException(resultDto.getMessage());
        }

        //更新用户信息，主要是 为了更新StartTestJobName
        //先取出来hogwartsTestUser.getStartTestJobName();,然后判断是否为空，为空才更新
        HogwartsTestUser hogwartsTestUser = resultDto.getData();

        hogwartsTestUserMapper.updateByPrimaryKeySelective(hogwartsTestUser);

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
