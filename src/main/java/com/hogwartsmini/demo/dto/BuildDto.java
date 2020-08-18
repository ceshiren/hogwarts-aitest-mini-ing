package com.hogwartsmini.demo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author tlibn
 * @Date 2020/7/16 17:27
 **/
@ApiModel(value = "用户登录类", description = "请求类")
@Data
public class BuildDto {

    @ApiModelProperty(value="job名称", example="hogwarts",required=true)
    private String jobName;

    @ApiModelProperty(value="用户id", example="hogwarts",required=true)
    private String userId;

    @ApiModelProperty(value="备注", example="hogwarts123",required=true)
    private String remark;

    @ApiModelProperty(value="测试命令", example="pwd",required=true)
    private String testCommand;

}
