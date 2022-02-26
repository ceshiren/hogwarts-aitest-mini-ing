package com.hogwartsmini.demo.dao;

import com.hogwartsmini.demo.common.MySqlExtensionMapper;
import com.hogwartsmini.demo.entity.HogwartsTestCase;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


@Repository
public interface HogwartsTestCaseMapper extends MySqlExtensionMapper<HogwartsTestCase> {

    /**
     *  统计总数
     * @param params
     * @return
     */
    Integer count(@Param("params")Map<String,Object> params);

    /**
     *  分页数据
     * @param params
     * @param pageNum 页码
     * @param pageSize 每页数据量
     * @return
     */
    List<HogwartsTestCase> list(@Param("params")Map<String,Object> params
            , @Param("pageNum") Integer pageNum, @Param("pageSize") Integer pageSize);

}
