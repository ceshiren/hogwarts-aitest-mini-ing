package com.hogwartsmini.demo.dao;

import com.hogwartsmini.demo.common.MySqlExtensionMapper;
import com.hogwartsmini.demo.entity.HogwartsTestJenkins;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface HogwartsTestJenkinsMapper extends MySqlExtensionMapper<HogwartsTestJenkins> {


    int count(@Param("params") Map params);

    List<HogwartsTestJenkins> list(@Param("params") Map params
            ,@Param("pageNum") Integer pageNum,@Param("pageSize") Integer pageSize);

}
