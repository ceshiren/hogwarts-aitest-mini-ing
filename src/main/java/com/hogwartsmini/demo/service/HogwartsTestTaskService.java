package com.hogwartsmini.demo.service;

import com.hogwartsmini.demo.common.ResultDto;
import com.hogwartsmini.demo.common.TokenDto;
import com.hogwartsmini.demo.dto.AllureReportDto;
import com.hogwartsmini.demo.dto.RequestInfoDto;
import com.hogwartsmini.demo.dto.task.TestTaskDto;
import com.hogwartsmini.demo.entity.HogwartsTestTask;

import java.io.IOException;
import java.net.URISyntaxException;


public interface HogwartsTestTaskService {

    /**
     *  新增测试任务信息
     * @param testTaskDto
     * @return
     */
    ResultDto<HogwartsTestTask> save(TestTaskDto testTaskDto, Integer taskType);

    /**
     *  开始执行测试任务信息
     * @param hogwartsTestTask
     * @return
     */
    ResultDto startTask(TokenDto tokenDto, RequestInfoDto requestInfoDto, HogwartsTestTask hogwartsTestTask) throws IOException, URISyntaxException;

    /**
     *  修改测试任务状态信息
     * @param hogwartsTestTask
     * @return
     */
    ResultDto<HogwartsTestTask> updateStatus(HogwartsTestTask hogwartsTestTask);

    /**
     *  获取allure报告
     * @param tokenDto
     * @param taskId
     * @return
     */
    ResultDto<AllureReportDto> getAllureReport(TokenDto tokenDto, Integer taskId);

}
