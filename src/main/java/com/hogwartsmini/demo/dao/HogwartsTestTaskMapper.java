package com.hogwartsmini.demo.dao;

import com.hogwartsmini.demo.common.MySqlExtensionMapper;
import com.hogwartsmini.demo.entity.HogwartsTestTask;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface HogwartsTestTaskMapper extends MySqlExtensionMapper<HogwartsTestTask> {

    /**
     * 统计总数
     * @param params
     * @return
     */
    Integer count(@Param("params") Map<String, Object> params);

    /**
     * 列表分页查询
     * @param params
     * @param pageNum
     * @param pageSize
     * @return
     */
    List<HogwartsTestTask> list(@Param("params") Map<String, Object> params, @Param("pageNum") Integer pageNum,
                                @Param("pageSize") Integer pageSize);

}
