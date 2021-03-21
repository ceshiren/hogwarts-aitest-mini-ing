package com.hogwartsmini.demo.service;

import com.hogwartsmini.demo.common.PageTableRequest;
import com.hogwartsmini.demo.common.PageTableResponse;
import com.hogwartsmini.demo.common.ResultDto;
import com.hogwartsmini.demo.dto.testcase.QueryHogwartsTestCaseListDto;
import com.hogwartsmini.demo.entity.HogwartsTestCase;
import org.springframework.stereotype.Service;

@Service
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
	ResultDto<HogwartsTestCase> delete(Integer caseId, Integer createUserId);

	/**
	 *  修改测试用例信息
	 * @param hogwartsTestCase
	 * @return
	 */
	ResultDto<HogwartsTestCase> update(HogwartsTestCase hogwartsTestCase);

	/**
	 *  根据id查询测试用例
	 * @param caseId
	 * @param createUserId
	 * @return
	 */
	ResultDto<HogwartsTestCase> getById(Integer caseId, Integer createUserId);

	/**
	 *  查询Jenkins信息列表
	 * @param pageTableRequest
	 * @return
	 */
	ResultDto<PageTableResponse<HogwartsTestCase>> list(PageTableRequest<QueryHogwartsTestCaseListDto> pageTableRequest);

	/**
	 *  根据用户id和caseId查询case原始数据-直接返回字符串，因为会保存为文件
	 * @param createUserId
	 * @param caseId
	 * @return
	 */
	String getCaseDataById(Integer createUserId, Integer caseId);

}
