package com.hogwartsmini.demo.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import javax.persistence.*;

@Data
@Table(name = "hogwarts_test_jenkins")
public class HogwartsTestJenkins extends BaseEntityNew {
    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 名称
     */
    private String name;

    /**
     * 测试命令
     */
    @Column(name = "test_command")
    private String testCommand;

    /**
     * Jenkins的baseUrl
     */
    private String url;

    /**
     * 用户名
     */
    @Column(name = "user_name")
    private String userName;

    /**
     * 密码
     */
    private String password;

    /**
     * 创建人id
     */
    @Column(name = "create_user_id")
    private Integer createUserId;

    /**
     * 命令运行的测试用例类型  1 文本 2 文件
     */
    @Column(name = "command_run_case_type")
    private Integer commandRunCaseType;

    /**
     * 备注
     */
    private String remark;

    /**
     * 测试用例后缀名 如果case为文件时，此处必填
     */
    @Column(name = "command_run_case_suffix")
    private String commandRunCaseSuffix;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @Column(name = "update_time")
    private Date updateTime;

    /**
     *  此字段不存入数据库
     * 是否设置为默认服务器 1 是 0 否
     */
    @Transient
    @ApiModelProperty(value="是否设置为默认服务器 1 是 0 否",required=true)
    private Integer defaultJenkinsFlag = 0;
}
