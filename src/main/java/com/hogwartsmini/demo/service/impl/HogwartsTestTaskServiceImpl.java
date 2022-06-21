package com.hogwartsmini.demo.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.hogwartsmini.demo.common.*;
import com.hogwartsmini.demo.dao.HogwartsTestCaseMapper;
import com.hogwartsmini.demo.dao.HogwartsTestTaskCaseRelMapper;
import com.hogwartsmini.demo.dao.HogwartsTestTaskMapper;
import com.hogwartsmini.demo.dao.HogwartsTestUserMapper;
import com.hogwartsmini.demo.dto.AllureReportDto;
import com.hogwartsmini.demo.dto.OperateJenkinsJobDto;
import com.hogwartsmini.demo.dto.RequestInfoDto;
import com.hogwartsmini.demo.dto.task.AddHogwartsTestTaskDto;
import com.hogwartsmini.demo.dto.task.TestTaskDto;
import com.hogwartsmini.demo.entity.HogwartsTestCase;
import com.hogwartsmini.demo.entity.HogwartsTestTask;
import com.hogwartsmini.demo.entity.HogwartsTestTaskCaseRel;
import com.hogwartsmini.demo.service.HogwartsTestTaskService;
import com.hogwartsmini.demo.util.JenkinsUtil;
import com.hogwartsmini.demo.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
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

        StringBuilder testCommand = new StringBuilder();

        AddHogwartsTestTaskDto testTask = testTaskDto.getTestTask();
        List<Integer> caseIdList = testTaskDto.getCaseIdList();

        //根据caseIdList获取测试用例列表
        List<HogwartsTestCase> hogwartsTestCaseList =  hogwartsTestCaseMapper.selectByIds(StrUtil.list2IdsStr(caseIdList));

        //生成测试命令
        makeTestCommand(testCommand, hogwartsTestCaseList);

        //测试任务存库
        HogwartsTestTask hogwartsTestTask = new HogwartsTestTask();
        hogwartsTestTask.setName(testTask.getName());
        hogwartsTestTask.setTestJenkinsId(testTask.getTestJenkinsId());
        hogwartsTestTask.setCreateUserId(testTask.getCreateUserId());
        hogwartsTestTask.setRemark(testTask.getRemark());
        hogwartsTestTask.setTaskType(taskType);
        hogwartsTestTask.setTestCommand(testCommand.toString());
        hogwartsTestTask.setCaseCount(caseIdList.size());
        hogwartsTestTask.setStatus(Constants.STATUS_ONE);
        hogwartsTestTask.setCreateTime(new Date());
        hogwartsTestTask.setUpdateTime(new Date());

        hogwartsTestTaskMapper.insert(hogwartsTestTask);

        //测试任务详情数据存库
        if(Objects.nonNull(caseIdList) && caseIdList.size()>0){

            List<HogwartsTestTaskCaseRel> testTaskCaseList = new ArrayList<>();

            for (Integer testCaseId:caseIdList) {

                HogwartsTestTaskCaseRel hogwartsTestTaskCaseRel = new HogwartsTestTaskCaseRel();
                hogwartsTestTaskCaseRel.setTaskId(hogwartsTestTask.getId());
                hogwartsTestTaskCaseRel.setCaseId(testCaseId);
                hogwartsTestTaskCaseRel.setCreateUserId(hogwartsTestTask.getCreateUserId());
                hogwartsTestTaskCaseRel.setCreateTime(new Date());
                hogwartsTestTaskCaseRel.setUpdateTime(new Date());
                testTaskCaseList.add(hogwartsTestTaskCaseRel);
            }

            log.info("=====测试任务详情保存-落库入参====："+ JSONObject.toJSONString(testTaskCaseList));
            hogwartsTestTaskCaseRelMapper.insertList(testTaskCaseList);
        }

        return ResultDto.success("成功", hogwartsTestTask);

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
        List<HogwartsTestTask> hogwartsTestJenkinsList = hogwartsTestTaskMapper.list(params,
                (pageNum - 1) * pageSize, pageSize);

        PageTableResponse<HogwartsTestTask> hogwartsTestJenkinsPageTableResponse = new PageTableResponse<>();
        hogwartsTestJenkinsPageTableResponse.setRecordsTotal(recordsTotal);
        hogwartsTestJenkinsPageTableResponse.setData(hogwartsTestJenkinsList);

        return ResultDto.success("成功", hogwartsTestJenkinsPageTableResponse);
    }

    /**
     * 开始执行测试任务信息
     *
     * @param requestInfoDto
     * @param hogwartsTestTask
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultDto startTask(RequestInfoDto requestInfoDto, HogwartsTestTask hogwartsTestTask) throws IOException {

        HogwartsTestTask query = new HogwartsTestTask();
        query.setId(hogwartsTestTask.getId());
        query.setCreateUserId(hogwartsTestTask.getCreateUserId());

        HogwartsTestTask result = hogwartsTestTaskMapper.selectOne(query);

        if(Objects.isNull(result)){
            return ResultDto.fail("任务不存在");
        }

        result.setStatus(Constants.STATUS_TWO);
        hogwartsTestTaskMapper.updateByPrimaryKeySelective(result);


        return ResultDto.success("成功");
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

    /**
     * 获取allure报告
     *
     * @param userId
     * @param taskId
     * @return
     */
    @Override
    public ResultDto<AllureReportDto> getAllureReport(Integer userId, Integer taskId) {

        HogwartsTestTask queryHogwartsTestTask = new HogwartsTestTask();

        queryHogwartsTestTask.setId(taskId);
        queryHogwartsTestTask.setCreateUserId(userId);

        HogwartsTestTask result = hogwartsTestTaskMapper.selectOne(queryHogwartsTestTask);

        //如果为空，则提示
        if (Objects.isNull(result)) {
            return ResultDto.fail("未查到测试任务信息");
        }

        String allureReportUrl = result.getBuildUrl();
        if(StringUtils.isEmpty(allureReportUrl)){
            return ResultDto.fail("测试任务的报告信息不存在");
        }

        AllureReportDto allureReportDto = new AllureReportDto();
        allureReportDto.setTaskId(taskId);

        OperateJenkinsJobDto operateJenkinsJobDto = new OperateJenkinsJobDto();
        operateJenkinsJobDto.setJenkinsUrl(jenkinsUrl);
        operateJenkinsJobDto.setJenkinsUserName(jenkinsUserName);
        operateJenkinsJobDto.setJenkinsPassword(jenkinsPassword);

        //"http://stuq.ceshiren.com:8080 /job/hogwarts_test_mini_start_test_1/31/ allure"
        allureReportDto.setAllureReportUrl(JenkinsUtil.getAllureReportUrl(allureReportUrl, operateJenkinsJobDto));

        return ResultDto.success("成功", allureReportDto);
    }

    /**
     *
     * @param testCommand
     * @param testCaseList
     */
    private void makeTestCommand(StringBuilder testCommand, List<HogwartsTestCase> testCaseList) {

        testCommand.append("pwd").append("\n");


        //文本
        if(jenkinsCaseType==1){

            for (HogwartsTestCase hogwartsTestCase:testCaseList) {
                testCommand.append(jenkinsTestCommand).append(" ");
                testCommand.append(hogwartsTestCase.getCaseData());
                testCommand.append("\n");
            }

        }
        //文件
        if(jenkinsCaseType==2){

            for (HogwartsTestCase hogwartsTestCase:testCaseList) {
                makeCurlCommand(testCommand, hogwartsTestCase, jenkinsCaseSuffix);

                testCommand.append(jenkinsTestCommand).append(" ").
                        append(hogwartsTestCase.getCaseName()).
                        append(".").
                        append(jenkinsCaseSuffix).
                        append(" || true").
                        append("\n");
            }


        }

    }

    /**
     *  拼装下载文件的curl命令
     * @param testCommand
     * @param hogwartsTestCase
     * @param commandRunCaseSuffix
     */
    private void makeCurlCommand(StringBuilder testCommand, HogwartsTestCase hogwartsTestCase, String commandRunCaseSuffix) {

        testCommand.append("curl ").append("-o ");
        String caseName = hogwartsTestCase.getCaseName();

        if(StringUtils.isEmpty(caseName)){
            caseName = "none";
        }
        testCommand.append(caseName).
                //http://stuq.ceshiren.com:8081/
                append(".").
                append(commandRunCaseSuffix).
                append(" ${aitestBaseUrl}/testCase/data/").
                append(hogwartsTestCase.getId()).append(" -H \"${token}\"");
        testCommand.append(" || true");
        testCommand.append("\n");


    }

}
