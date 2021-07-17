package com.hogwartsmini.demo.service.impl;

import com.hogwartsmini.demo.common.*;
import com.hogwartsmini.demo.dao.HogwartsTestJenkinsMapper;
import com.hogwartsmini.demo.dao.HogwartsTestUserMapper;
import com.hogwartsmini.demo.dto.UserDto;
import com.hogwartsmini.demo.dto.jenkins.QueryHogwartsTestJenkinsListDto;
import com.hogwartsmini.demo.entity.HogwartsTestJenkins;
import com.hogwartsmini.demo.entity.HogwartsTestUser;
import com.hogwartsmini.demo.service.HogwartsTestJenkinsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;
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
    private HogwartsTestUserMapper hogwartsTestUserMapper;

    @Autowired
    private TokenDb tokenDb;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultDto<HogwartsTestJenkins> save(HogwartsTestJenkins hogwartsTestJenkins){

        //设置创建时间和更新时间
        hogwartsTestJenkins.setCreateTime(new Date());
        hogwartsTestJenkins.setUpdateTime(new Date());
        //将Jenkins数据存入数据库
        hogwartsTestJenkinsMapper.insertUseGeneratedKeys(hogwartsTestJenkins);

        //如果是否为默认Jenkins的标志位为1
        // ，则修改hogwarts_test_user中的default_jenkins_id字段

        Integer defaultJenkinsFlag = hogwartsTestJenkins.getDefaultJenkinsFlag();

        if(Objects.nonNull(defaultJenkinsFlag)&&defaultJenkinsFlag==1){
            //从tokenDb中获取用户信息(包含用户id)

            Integer createUserId = hogwartsTestJenkins.getCreateUserId();

            HogwartsTestUser hogwartsTestUser = new HogwartsTestUser();
            hogwartsTestUser.setId(createUserId);

            HogwartsTestUser resultHogwartsTestUser = hogwartsTestUserMapper.selectOne(hogwartsTestUser);

            if(Objects.isNull(resultHogwartsTestUser)){
                return ResultDto.fail("用户未找到");
            }

            //将新增的JenkinsId放入default_jenkins_id字段，并根据用户id更新hogwarts_test_user，
            resultHogwartsTestUser.setDefaultJenkinsId(hogwartsTestJenkins.getId());
            //更新语句
            hogwartsTestUserMapper.updateByPrimaryKeySelective(resultHogwartsTestUser);

        }

        return ResultDto.success("成功",hogwartsTestJenkins);
    }

    /**
     * 分页查询Jenkins列表
     *
     * @param pageTableRequest
     * @return
     */
    @Override
    public ResultDto<PageTableResponse<HogwartsTestJenkins>> list(PageTableRequest pageTableRequest) {

        //参数校验

        //获取参数
        Integer pageNum =pageTableRequest.getPageNum();
        Integer pageSize = pageTableRequest.getPageSize();
        Map params = pageTableRequest.getParams();

        //获取数据总数量
        int count = hogwartsTestJenkinsMapper.count(params);
        //获取数据列表
        List<HogwartsTestJenkins> hogwartsTestJenkinsList = hogwartsTestJenkinsMapper
                .list(params, (pageNum-1)*pageSize, pageSize);

        //给响应参数赋值
        PageTableResponse response = new PageTableResponse();
        response.setRecordsTotal(count);
        response.setData(hogwartsTestJenkinsList);
        //返回
        return ResultDto.success("成功",response);
    }
}
