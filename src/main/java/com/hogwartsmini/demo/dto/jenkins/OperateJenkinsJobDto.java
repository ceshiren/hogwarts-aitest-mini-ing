package com.hogwartsmini.demo.dto.jenkins;

import com.hogwartsmini.demo.common.TokenDto;
import com.hogwartsmini.demo.entity.HogwartsTestJenkins;
import com.hogwartsmini.demo.entity.HogwartsTestUser;
import lombok.Data;

import java.util.Map;

/**
 * @Author tlibn
 * @Date 2020/2/6 13:09
 **/
@Data
public class OperateJenkinsJobDto {


    private TokenDto tokenDto;

    private HogwartsTestJenkins hogwartsTestJenkins;

    private HogwartsTestUser hogwartsTestUser;

    //构建参数
    private Map<String, String> params;

}
