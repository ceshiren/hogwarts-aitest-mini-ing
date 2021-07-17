package com.hogwartsmini.demo.dao;

import com.hogwartsmini.demo.common.MySqlExtensionMapper;
import com.hogwartsmini.demo.entity.HogwartsTestCase;
import org.springframework.stereotype.Repository;

@Repository
public interface HogwartsTestCaseMapper extends MySqlExtensionMapper<HogwartsTestCase> {
}
