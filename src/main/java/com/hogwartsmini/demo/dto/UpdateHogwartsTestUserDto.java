package com.hogwartsmini.demo.dto;

import com.hogwartsmini.demo.entity.BaseEntityNew;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(value = "用户信息修改类", description = "请求类")
@Data
public class UpdateHogwartsTestUserDto extends BaseEntityNew {

    /**
     * 用户id
     */
    @ApiModelProperty(value="用户id", example="12",required=true)
    private Integer id;

    /**
     * 用户名
     */
    @ApiModelProperty(value="用户名", example="hogwarts",required=true)
    private String userName;

    /**
     * 密码
     */
    @ApiModelProperty(value="密码", example="hogwarts",required=true)
    private String password;

    /**
     * 邮箱
     */
    @ApiModelProperty(value="邮箱", example="hogwarts",required=true)
    private String email;

    /**
     * 自动生成用例job名称 不为空时表示已经创建job
     */
    @ApiModelProperty(value="用户名", example="hogwarts")
    private String autoCreateCaseJobName;

    /**
     * 执行测试job名称 不为空时表示已经创建job
     */
    @ApiModelProperty(value="用户名", example="hogwarts")
    private String startTestJobName;

    /**
     * 默认Jenkins服务器
     */
    @ApiModelProperty(value="用户名", example="hogwarts")
    private Integer defaultJenkinsId;

}
