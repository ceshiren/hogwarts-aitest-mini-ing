package com.hogwartsmini.demo.dao;

import com.hogwartsmini.demo.common.MySqlExtensionMapper;
import com.hogwartsmini.demo.entity.HogwartsTestUser;
import org.apache.ibatis.annotations.Param;

public interface HogwartsTestUserMapper extends MySqlExtensionMapper<HogwartsTestUser> {

    //HogwartsTestUser selectHogwartsTestUser(@Param("id") Integer id);
}
