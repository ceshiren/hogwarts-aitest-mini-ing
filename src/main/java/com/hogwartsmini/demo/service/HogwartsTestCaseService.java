package com.hogwartsmini.demo.service;

import com.hogwartsmini.demo.common.PageTableRequest;
import com.hogwartsmini.demo.common.PageTableResponse;
import com.hogwartsmini.demo.common.ResultDto;
import com.hogwartsmini.demo.entity.HogwartsTestCase;

public interface HogwartsTestCaseService {

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
     *  列表查询
     * @param pageTableRequest
     * @return
     */
    ResultDto<PageTableResponse<HogwartsTestCase>> list(PageTableRequest pageTableRequest);

    ResultDto<HogwartsTestCase> getById(HogwartsTestCase hogwartsTestCase);

    /**
     *  根据用例id删除
     * @param hogwartsTestCase
     * @return
     */
    ResultDto delete(HogwartsTestCase hogwartsTestCase);
}
