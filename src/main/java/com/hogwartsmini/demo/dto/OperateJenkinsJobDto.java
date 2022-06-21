package com.hogwartsmini.demo.dto;

import com.hogwartsmini.demo.entity.HogwartsTestUser;
import lombok.Data;

import java.util.Map;

/**
 * @Author tlibn
 * @Date 2020/2/6 13:09
 **/
@Data
public class OperateJenkinsJobDto {


    private String token;


    private String jenkinsUrl;

    private String jenkinsUserName;

    private String jenkinsPassword;

    private HogwartsTestUser hogwartsTestUser;

    //构建参数
    private Map<String, String> params;

}
