package com.hogwartsmini.demo.service;


import com.hogwartsmini.demo.common.PageTableRequest;
import com.hogwartsmini.demo.common.PageTableResponse;
import com.hogwartsmini.demo.common.ResultDto;
import com.hogwartsmini.demo.dto.RequestInfoDto;
import com.hogwartsmini.demo.dto.task.TestTaskDto;
import com.hogwartsmini.demo.entity.HogwartsTestTask;

public interface HogwartsTestTaskService {

	/**
	 *  新增测试任务信息
	 * @param testTaskDto
	 * @return
	 */
	ResultDto<HogwartsTestTask> save(TestTaskDto testTaskDto, Integer taskType);

	/**
	 *  删除测试任务信息
	 * @param taskId
	 * @param createUserId
	 * @return
	 */
	ResultDto<HogwartsTestTask> delete(Integer taskId, Integer createUserId);

	/**
	 *  修改测试任务信息
	 * @param hogwartsTestTask
	 * @return
	 */
	ResultDto<HogwartsTestTask> update(HogwartsTestTask hogwartsTestTask);

	/**
	 *  根据id查询
	 * @param taskId
	 * @return
	 */
	ResultDto<HogwartsTestTask> getById(Integer taskId, Integer createUserId);

	/**
	 *  查询测试任务信息列表
	 * @param pageTableRequest
	 * @return
	 */
	ResultDto<PageTableResponse<HogwartsTestTask>> list(PageTableRequest pageTableRequest);

	/**
	 *  修改测试任务状态信息
	 * @param hogwartsTestTask
	 * @return
	 */
	ResultDto<HogwartsTestTask> updateStatus(HogwartsTestTask hogwartsTestTask);

	/**
	 *  执行测试接口
	 * @param hogwartsTestTask
	 * @return
	 */
	ResultDto<HogwartsTestTask> startTest(RequestInfoDto requestInfoDto, HogwartsTestTask hogwartsTestTask);


	/**
	 *  获取allure报告地址接口
	 * @param hogwartsTestTask
	 * @return
	 */
	ResultDto<String> getAllureReport(HogwartsTestTask hogwartsTestTask);



}
