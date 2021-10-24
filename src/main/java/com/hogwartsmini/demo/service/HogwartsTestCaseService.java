package com.hogwartsmini.demo.service;


import com.hogwartsmini.demo.common.PageTableRequest;
import com.hogwartsmini.demo.common.PageTableResponse;
import com.hogwartsmini.demo.common.ResultDto;
import com.hogwartsmini.demo.dto.QueryHogwartsTestCaseListDto;
import com.hogwartsmini.demo.dto.RunCaseDto;
import com.hogwartsmini.demo.entity.HogwartsTestCase;

public interface HogwartsTestCaseService {

	/**
	 *  新增测试用例
	 * @param hogwartsTestCase
	 * @return
	 */
	ResultDto save(HogwartsTestCase hogwartsTestCase);

	/**
	 *  删除测试用例信息
	 * @param caseId
	 * @param createUserId
	 * @return
	 */
	ResultDto<HogwartsTestCase> delete(Integer caseId,Integer createUserId);

	/**
	 *  修改测试用例信息
	 * @param hogwartsTestCase
	 * @return
	 */
	ResultDto<HogwartsTestCase> update(HogwartsTestCase hogwartsTestCase);

	/**
	 *  根据id查询测试用例
	 * @param jenkinsId
	 * @param createUserId
	 * @return
	 */
	ResultDto<HogwartsTestCase> getById(Integer caseId,Integer createUserId);

	/**
	 *  查询Jenkins信息列表
	 * @param pageTableRequest
	 * @return
	 */
	ResultDto<PageTableResponse<HogwartsTestCase>> list(PageTableRequest<QueryHogwartsTestCaseListDto> pageTableRequest);

	/**
	 *  运行jmeter测试用例
	 * @param runCaseDto
	 * @return
	 */
	ResultDto runCase3(RunCaseDto runCaseDto) throws Exception;

}
