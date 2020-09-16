package com.hogwartsmini.demo.dao;

import com.hogwartsmini.demo.common.MySqlExtensionMapper;
import com.hogwartsmini.demo.dto.jenkins.QueryHogwartsTestJenkinsListDto;
import com.hogwartsmini.demo.entity.HogwartsTestJenkins;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HogwartsTestJenkinsMapper extends MySqlExtensionMapper<HogwartsTestJenkins> {

    /**
     *  统计总数
     * @param params
     * @return
     */
    Integer count(@Param("params")QueryHogwartsTestJenkinsListDto params);

    /**
     *  统计总数
     * @param params
     * @return
     */
    List<HogwartsTestJenkins> list(@Param("params")QueryHogwartsTestJenkinsListDto params
    ,@Param("pageNum")Integer pageNum,@Param("pageSize")Integer pageSize);
}
