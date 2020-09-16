package com.hogwartsmini.demo.dto.jenkins;

import com.hogwartsmini.demo.common.TokenDto;
import com.hogwartsmini.demo.entity.HogwartsTestJenkins;
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

    //构建参数
    private Map<String, String> params;

}
