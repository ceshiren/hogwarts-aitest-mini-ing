package com.hogwartsmini.demo.dao;

import com.hogwartsmini.demo.common.MySqlExtensionMapper;
import com.hogwartsmini.demo.dto.testcase.QueryHogwartsTestCaseListDto;
import com.hogwartsmini.demo.entity.HogwartsTestCase;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HogwartsTestCaseMapper extends MySqlExtensionMapper<HogwartsTestCase> {

    List<HogwartsTestCase> getByIdList(@Param("createUserId") Integer createUserId, @Param("list") List<Integer> list);

    /**
     * 统计总数
     * @param params
     * @return
     */
    Integer count(@Param("params") QueryHogwartsTestCaseListDto params);

    /**
     * 列表分页查询
     * @param params
     * @param pageNum
     * @param pageSize
     * @return
     */
    List<HogwartsTestCase> list(@Param("params") QueryHogwartsTestCaseListDto params, @Param("pageNum") Integer pageNum,
                                @Param("pageSize") Integer pageSize);
}
