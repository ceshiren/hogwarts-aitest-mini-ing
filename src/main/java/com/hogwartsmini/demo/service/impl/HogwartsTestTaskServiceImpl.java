package com.hogwartsmini.demo.service.impl;

import com.hogwartsmini.demo.common.*;
import com.hogwartsmini.demo.dao.HogwartsTestCaseMapper;
import com.hogwartsmini.demo.dao.HogwartsTestTaskCaseRelMapper;
import com.hogwartsmini.demo.dao.HogwartsTestTaskMapper;
import com.hogwartsmini.demo.dao.HogwartsTestUserMapper;
import com.hogwartsmini.demo.dto.task.AddHogwartsTestTaskDto;
import com.hogwartsmini.demo.dto.task.TestTaskDto;
import com.hogwartsmini.demo.entity.HogwartsTestCase;
import com.hogwartsmini.demo.entity.HogwartsTestTask;
import com.hogwartsmini.demo.entity.HogwartsTestTaskCaseRel;
import com.hogwartsmini.demo.service.HogwartsTestTaskService;
import com.hogwartsmini.demo.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;

@Slf4j
@Service
public class HogwartsTestTaskServiceImpl implements HogwartsTestTaskService {

    @Autowired
    private HogwartsTestTaskMapper hogwartsTestTaskMapper;

    @Autowired
    private HogwartsTestCaseMapper hogwartsTestCaseMapper;

    @Autowired
    private HogwartsTestTaskCaseRelMapper hogwartsTestTaskCaseRelMapper;

    @Autowired
    private HogwartsTestUserMapper hogwartsTestUserMapper;


    @Value("${jenkins.url}")
    private String jenkinsUrl;
    @Value("${jenkins.username}")
    private String jenkinsUserName;
    @Value("${jenkins.password}")
    private String jenkinsPassword;
    @Value("${jenkins.casetype}")
    private Integer jenkinsCaseType;
    @Value("${jenkins.casesuffix}")
    private String jenkinsCaseSuffix;
    @Value("${jenkins.testcommand}")
    private String jenkinsTestCommand;

    /**
     * 新增测试任务信息
     *
     * @param testTaskDto
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultDto<HogwartsTestTask> save(TestTaskDto testTaskDto, Integer taskType) {

        List<Integer> caseIdList = testTaskDto.getCaseIdList();
        AddHogwartsTestTaskDto testTask = testTaskDto.getTestTask();

        String ids = StrUtil.list2Ids(caseIdList);

        StringBuilder testCommand = new StringBuilder();
        List<HogwartsTestCase> hogwartsTestCaseList = hogwartsTestCaseMapper.selectByIds(ids);

        makeTestCommand(testCommand,hogwartsTestCaseList);


        HogwartsTestTask hogwartsTestTask = new HogwartsTestTask();

        hogwartsTestTask.setStatus(Constants.STATUS_ONE);
        hogwartsTestTask.setCreateUserId(testTask.getCreateUserId());
        hogwartsTestTask.setTestCommand(testCommand.toString());
        hogwartsTestTask.setCaseCount(caseIdList.size());
        hogwartsTestTask.setName(testTask.getName());
        hogwartsTestTask.setCreateTime(new Date());
        hogwartsTestTask.setUpdateTime(new Date());
        hogwartsTestTask.setTaskType(1);
        hogwartsTestTask.setTestJenkinsId(1);
        hogwartsTestTask.setRemark(testTask.getRemark());

        hogwartsTestTaskMapper.insertUseGeneratedKeys(hogwartsTestTask);

        List<HogwartsTestTaskCaseRel> hogwartsTestTaskCaseRelList = new ArrayList<>();

        for (Integer caseId:caseIdList) {
            HogwartsTestTaskCaseRel hogwartsTestTaskCaseRel = new HogwartsTestTaskCaseRel();

            hogwartsTestTaskCaseRel.setTaskId(hogwartsTestTask.getId());
            hogwartsTestTaskCaseRel.setCaseId(caseId);
            hogwartsTestTaskCaseRel.setCreateTime(new Date());
            hogwartsTestTaskCaseRel.setUpdateTime(new Date());
            hogwartsTestTaskCaseRel.setCreateUserId(testTask.getCreateUserId());

            hogwartsTestTaskCaseRelList.add(hogwartsTestTaskCaseRel);

        }

        hogwartsTestTaskCaseRelMapper.insertList(hogwartsTestTaskCaseRelList);

        return ResultDto.success("成功",hogwartsTestTask);
    }

    private void makeTestCommand(StringBuilder testCommand, List<HogwartsTestCase> hogwartsTestCaseList) {

        testCommand.append("pwd");
        testCommand.append("\n");

        String sysCommand = jenkinsTestCommand + " --alluredir=${WORKSPACE}/target/allure-results";

        if(jenkinsCaseType==1){
            for (HogwartsTestCase hogwartsTestCase:hogwartsTestCaseList) {
                testCommand.append(sysCommand).append(" ");
                testCommand.append(hogwartsTestCase.getCaseData()).append("\n");
            }

        }

        if(jenkinsCaseType==2){
            for (HogwartsTestCase hogwartsTestCase:hogwartsTestCaseList) {

                makeCurlCommand(testCommand, hogwartsTestCase, jenkinsCaseSuffix);
                testCommand.append("\n");

                testCommand.append(sysCommand).append(" ");

                testCommand.append(hogwartsTestCase.getCaseName())
                        .append(".")
                        .append(jenkinsCaseSuffix)
                        .append(" || true")
                        .append("\n");

            }

        }

        testCommand.append("\n");


    }

    /**
     *  拼装下载文件的curl命令
     * @param testCommand
     * @param hogwartsTestCase
     * @param commandRunCaseSuffix
     */
    private void makeCurlCommand(StringBuilder testCommand, HogwartsTestCase hogwartsTestCase, String commandRunCaseSuffix) {

        //通过curl命令获取测试数据并保存为文件
        testCommand.append("curl ")
                .append("-o ");

        String caseName = hogwartsTestCase.getCaseName();

        if(StringUtils.isEmpty(caseName)){
            caseName = "测试用例无测试名称";
        }

        testCommand.append(caseName)
                .append(".")
                .append(commandRunCaseSuffix)
                .append(" ${aitestBaseUrl}/testCase/data/")
                .append(hogwartsTestCase.getId())
                .append(" -H \"token: ${token}\" ");

        //本行命令执行失败，继续运行下面的命令行
        testCommand.append(" || true");

        testCommand.append("\n");
    }

    /**
     * 删除测试任务信息
     *
     * @param taskId
     * @param createUserId
     * @return
     */
    @Override
    public ResultDto<HogwartsTestTask> delete(Integer taskId, Integer createUserId) {
        HogwartsTestTask queryHogwartsTestTask = new HogwartsTestTask();

        queryHogwartsTestTask.setId(taskId);
        queryHogwartsTestTask.setCreateUserId(createUserId);

        HogwartsTestTask result = hogwartsTestTaskMapper.selectOne(queryHogwartsTestTask);

        //如果为空，则提示，也可以直接返回成功
        if (Objects.isNull(result)) {
            return ResultDto.fail("未查到测试任务信息");
        }
        hogwartsTestTaskMapper.deleteByPrimaryKey(taskId);

        return ResultDto.success("成功");
    }

    /**
     * 修改测试任务信息
     *
     * @param hogwartsTestTask
     * @return
     */
    @Override
    public ResultDto<HogwartsTestTask> update(HogwartsTestTask hogwartsTestTask) {
        HogwartsTestTask queryHogwartsTestTask = new HogwartsTestTask();

        queryHogwartsTestTask.setId(hogwartsTestTask.getId());
        queryHogwartsTestTask.setCreateUserId(hogwartsTestTask.getCreateUserId());

        HogwartsTestTask result = hogwartsTestTaskMapper.selectOne(queryHogwartsTestTask);

        //如果为空，则提示，也可以直接返回成功
        if (Objects.isNull(result)) {
            return ResultDto.fail("未查到测试任务信息");
        }

        result.setUpdateTime(new Date());
        result.setName(hogwartsTestTask.getName());
        result.setRemark(hogwartsTestTask.getRemark());

        hogwartsTestTaskMapper.updateByPrimaryKeySelective(result);

        return ResultDto.success("成功");
    }

    /**
     * 根据id查询
     *
     * @param taskId
     * @param createUserId
     * @return
     */
    @Override
    public ResultDto<HogwartsTestTask> getById(Integer taskId, Integer createUserId) {
        HogwartsTestTask queryHogwartsTestTask = new HogwartsTestTask();

        queryHogwartsTestTask.setId(taskId);
        queryHogwartsTestTask.setCreateUserId(createUserId);

        HogwartsTestTask result = hogwartsTestTaskMapper.selectOne(queryHogwartsTestTask);

        //如果为空，则提示，也可以直接返回成功
        if (Objects.isNull(result)) {
            ResultDto.fail("未查到测试任务信息");
        }

        return ResultDto.success("成功", result);
    }

    /**
     * 查询测试任务信息列表
     *
     * @param pageTableRequest
     * @return
     */
    @Override
    public ResultDto<PageTableResponse<HogwartsTestTask>> list(PageTableRequest pageTableRequest) {
        Map<String, Object> params = pageTableRequest.getParams();
        Integer pageNum = pageTableRequest.getPageNum();
        Integer pageSize = pageTableRequest.getPageSize();

        //总数
        Integer recordsTotal = hogwartsTestTaskMapper.count(params);

        //分页查询数据
        List<HogwartsTestTask> hogwartsTestJenkinsList = hogwartsTestTaskMapper
                .list(params, (pageNum - 1) * pageSize, pageSize);

        PageTableResponse<HogwartsTestTask> hogwartsTestJenkinsPageTableResponse = new PageTableResponse<>();
        hogwartsTestJenkinsPageTableResponse.setRecordsTotal(recordsTotal);
        hogwartsTestJenkinsPageTableResponse.setData(hogwartsTestJenkinsList);

        return ResultDto.success("成功", hogwartsTestJenkinsPageTableResponse);
    }

    /**
     * 修改测试任务状态信息
     *
     * @param hogwartsTestTask
     * @return
     */
    @Override
    public ResultDto<HogwartsTestTask> updateStatus(HogwartsTestTask hogwartsTestTask) {
        HogwartsTestTask queryHogwartsTestTask = new HogwartsTestTask();

        queryHogwartsTestTask.setId(hogwartsTestTask.getId());
        queryHogwartsTestTask.setCreateUserId(hogwartsTestTask.getCreateUserId());

        HogwartsTestTask result = hogwartsTestTaskMapper.selectOne(queryHogwartsTestTask);

        //如果为空，则提示
        if (Objects.isNull(result)) {
            return ResultDto.fail("未查到测试任务信息");
        }

        //如果任务已经完成，则不重复修改
        if(Constants.STATUS_THREE.equals(result.getStatus())){
            return ResultDto.fail("测试任务已完成，无需修改");
        }

        result.setUpdateTime(new Date());

        //仅状态为已完成时修改
        if(Constants.STATUS_THREE.equals(hogwartsTestTask.getStatus())){
            result.setBuildUrl(hogwartsTestTask.getBuildUrl());
            result.setStatus(Constants.STATUS_THREE);
            hogwartsTestTaskMapper.updateByPrimaryKey(result);
        }

        return ResultDto.success("成功");
    }

}
