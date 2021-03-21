package com.hogwartsmini.demo.service.impl;

import com.hogwartsmini.demo.common.*;
import com.hogwartsmini.demo.dao.*;
import com.hogwartsmini.demo.dto.AllureReportDto;
import com.hogwartsmini.demo.dto.RequestInfoDto;
import com.hogwartsmini.demo.dto.jenkins.OperateJenkinsJobDto;
import com.hogwartsmini.demo.dto.task.AddHogwartsTestTaskDto;
import com.hogwartsmini.demo.dto.task.TestTaskDto;
import com.hogwartsmini.demo.entity.*;
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
