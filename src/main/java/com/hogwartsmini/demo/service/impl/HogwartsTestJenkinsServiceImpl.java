package com.hogwartsmini.demo.service.impl;

import com.hogwartsmini.demo.common.*;
import com.hogwartsmini.demo.dao.HogwartsTestJenkinsMapper;
import com.hogwartsmini.demo.dao.HogwartsTestUserMapper;
import com.hogwartsmini.demo.dto.UserDto;
import com.hogwartsmini.demo.dto.jenkins.QueryHogwartsTestJenkinsListDto;
import com.hogwartsmini.demo.entity.HogwartsTestJenkins;
import com.hogwartsmini.demo.service.HogwartsTestJenkinsService;
import com.hogwartsmini.demo.service.HogwartsTestUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @Author tlibn
 * @Date 2020/7/17 11:03
 **/
@Service
public class HogwartsTestJenkinsServiceImpl implements HogwartsTestJenkinsService {

    @Autowired
    private HogwartsTestJenkinsMapper hogwartsTestJenkinsMapper;

    @Autowired
    private TokenDb tokenDb;

    @Override
    public ResultDto<HogwartsTestJenkins> save(HogwartsTestJenkins hogwartsTestJenkins){

        //设置创建时间和更新时间
        hogwartsTestJenkins.setCreateTime(new Date());
        hogwartsTestJenkins.setUpdateTime(new Date());
        //将Jenkins数据存入数据库
        hogwartsTestJenkinsMapper.insertUseGeneratedKeys(hogwartsTestJenkins);

        //如果是否为默认Jenkins的标志位为1，则修改hogwarts_test_user中的default_jenkins_id字段

        //从tokenDb中获取用户信息(包含用户id)

        //将新增的JenkinsId放入default_jenkins_id字段，并根据用户id更新hogwarts_test_user，

        //更新语句

        return ResultDto.success("成功",hogwartsTestJenkins);
    }

    /**
     * 分页查询Jenkins列表
     *
     * @param pageTableRequest
     * @return
     */
    @Override
    public ResultDto<PageTableResponse<HogwartsTestJenkins>> list(PageTableRequest<QueryHogwartsTestJenkinsListDto> pageTableRequest) {

        //参数校验

        //获取参数
        Integer pageNum =pageTableRequest.getPageNum();
        Integer pageSize = pageTableRequest.getPageSize();
        QueryHogwartsTestJenkinsListDto params = pageTableRequest.getParams();

        //分页列表查询
        List<HogwartsTestJenkins> list = hogwartsTestJenkinsMapper
                .list(params, (pageNum - 1) * pageSize, pageSize);

        //计数查询
        Integer count = hogwartsTestJenkinsMapper.count(params);

        //组织分页列表查询响应
        PageTableResponse pageTableResponse = new PageTableResponse();
        pageTableResponse.setData(list);
        pageTableResponse.setRecordsTotal(count);

        //返回
        return ResultDto.success("成功",pageTableResponse);
    }
}
