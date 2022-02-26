package com.hogwartsmini.demo.dao;

import com.hogwartsmini.demo.common.MySqlExtensionMapper;
import com.hogwartsmini.demo.dto.testcase.HogwartsTestTaskCaseRelDetailDto;
import com.hogwartsmini.demo.entity.HogwartsTestTaskCaseRel;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface HogwartsTestTaskCaseRelMapper extends MySqlExtensionMapper<HogwartsTestTaskCaseRel> {

    /**
     * 列表分页查询
     * @param params
     * @param pageNum
     * @param pageSize
     * @return
     */
    List<HogwartsTestTaskCaseRelDetailDto> listDetail(@Param("params") Map<String,Object> params, @Param("pageNum") Integer pageNum,
                                                      @Param("pageSize") Integer pageSize);


}
