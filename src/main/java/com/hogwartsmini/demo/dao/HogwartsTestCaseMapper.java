package com.hogwartsmini.demo.dao;

import com.hogwartsmini.demo.common.MySqlExtensionMapper;
import com.hogwartsmini.demo.entity.HogwartsTestCase;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface HogwartsTestCaseMapper extends MySqlExtensionMapper<HogwartsTestCase> {


    Integer count(@Param("params") Map<String,Object> params);

    List<HogwartsTestCase> list(@Param("params") Map<String,Object> params,
                          @Param("pageNum") Integer pageNum,
                          @Param("pageSize") Integer pageSize);

}
