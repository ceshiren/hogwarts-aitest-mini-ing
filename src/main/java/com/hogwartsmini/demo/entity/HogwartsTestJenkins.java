package com.hogwartsmini.demo.entity;

import java.util.Date;
import javax.persistence.*;

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
    private Byte commandRunCaseType;

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
     * 获取主键
     *
     * @return id - 主键
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置主键
     *
     * @param id 主键
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取名称
     *
     * @return name - 名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置名称
     *
     * @param name 名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取测试命令
     *
     * @return test_command - 测试命令
     */
    public String getTestCommand() {
        return testCommand;
    }

    /**
     * 设置测试命令
     *
     * @param testCommand 测试命令
     */
    public void setTestCommand(String testCommand) {
        this.testCommand = testCommand;
    }

    /**
     * 获取Jenkins的baseUrl
     *
     * @return url - Jenkins的baseUrl
     */
    public String getUrl() {
        return url;
    }

    /**
     * 设置Jenkins的baseUrl
     *
     * @param url Jenkins的baseUrl
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * 获取用户名
     *
     * @return user_name - 用户名
     */
    public String getUserName() {
        return userName;
    }

    /**
     * 设置用户名
     *
     * @param userName 用户名
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * 获取密码
     *
     * @return password - 密码
     */
    public String getPassword() {
        return password;
    }

    /**
     * 设置密码
     *
     * @param password 密码
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 获取创建人id
     *
     * @return create_user_id - 创建人id
     */
    public Integer getCreateUserId() {
        return createUserId;
    }

    /**
     * 设置创建人id
     *
     * @param createUserId 创建人id
     */
    public void setCreateUserId(Integer createUserId) {
        this.createUserId = createUserId;
    }

    /**
     * 获取命令运行的测试用例类型  1 文本 2 文件
     *
     * @return command_run_case_type - 命令运行的测试用例类型  1 文本 2 文件
     */
    public Byte getCommandRunCaseType() {
        return commandRunCaseType;
    }

    /**
     * 设置命令运行的测试用例类型  1 文本 2 文件
     *
     * @param commandRunCaseType 命令运行的测试用例类型  1 文本 2 文件
     */
    public void setCommandRunCaseType(Byte commandRunCaseType) {
        this.commandRunCaseType = commandRunCaseType;
    }

    /**
     * 获取备注
     *
     * @return remark - 备注
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 设置备注
     *
     * @param remark 备注
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * 获取测试用例后缀名 如果case为文件时，此处必填
     *
     * @return command_run_case_suffix - 测试用例后缀名 如果case为文件时，此处必填
     */
    public String getCommandRunCaseSuffix() {
        return commandRunCaseSuffix;
    }

    /**
     * 设置测试用例后缀名 如果case为文件时，此处必填
     *
     * @param commandRunCaseSuffix 测试用例后缀名 如果case为文件时，此处必填
     */
    public void setCommandRunCaseSuffix(String commandRunCaseSuffix) {
        this.commandRunCaseSuffix = commandRunCaseSuffix;
    }

    /**
     * 获取创建时间
     *
     * @return create_time - 创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 设置创建时间
     *
     * @param createTime 创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取更新时间
     *
     * @return update_time - 更新时间
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * 设置更新时间
     *
     * @param updateTime 更新时间
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}