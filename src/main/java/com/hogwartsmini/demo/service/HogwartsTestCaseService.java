package com.hogwartsmini.demo.service;

import com.hogwartsmini.demo.common.PageTableRequest;
import com.hogwartsmini.demo.common.PageTableResponse;
import com.hogwartsmini.demo.common.ResultDto;
import com.hogwartsmini.demo.entity.HogwartsTestCase;

public interface HogwartsTestCaseService {

    /**
     *  列表查询
     * @param request
     * @return
     */
    ResultDto<PageTableResponse<HogwartsTestCase>> list(PageTableRequest request);

    /**
     *  保存
     * @param hogwartsTestCase
     * @return
     */
    ResultDto<HogwartsTestCase> save(HogwartsTestCase hogwartsTestCase);

    /**
     *  更新
     * @param hogwartsTestCase
     * @return
     */
    ResultDto<HogwartsTestCase> update(HogwartsTestCase hogwartsTestCase);

    /**
     *  根据id查询
     * @param caseId
     * @return
     */
    ResultDto getById(Integer caseId);

    /**
     *  根据id查询caseData
     * @param caseId
     * @return
     */
    String getDataById(Integer caseId);

    /**
     *  删除
     * @param caseId
     * @return
     */
    ResultDto delete(Integer caseId);
}
