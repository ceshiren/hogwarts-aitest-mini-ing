package com.hogwartsmini.demo.service.impl;

import com.hogwartsmini.demo.common.ResultDto;
import com.hogwartsmini.demo.dao.HogwartsTestUserMapper;
import com.hogwartsmini.demo.dto.UserDto;
import com.hogwartsmini.demo.entity.HogwartsTestUser;
import com.hogwartsmini.demo.service.HogwartsTestUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @Author tlibn
 * @Date 2020/7/17 11:03
 **/
@Service
public class HogwartsTestUserServiceImpl implements HogwartsTestUserService {

    @Autowired
    private HogwartsTestUserMapper hogwartsTestUserMapper;

    @Override
    public String login(UserDto userDto){

        System.out.println("userDto.getName()" + userDto.getUserName());
        System.out.println("userDto.getPwd()" + userDto.getPassword());

        return userDto.getName() + "-" + userDto.getPwd();
    }

    /**
     * 保存
     *
     * @param hogwartsTestUser
     * @return
     */
    @Override
    public ResultDto<HogwartsTestUser> save(HogwartsTestUser hogwartsTestUser) {

        hogwartsTestUser.setCreateTime(new Date());
        hogwartsTestUser.setUpdateTime(new Date());
        hogwartsTestUserMapper.insertUseGeneratedKeys(hogwartsTestUser);

        return ResultDto.success("成功",hogwartsTestUser);
    }

    /**
     * 更新
     *
     * @param hogwartsTestUser
     * @return
     */
    @Override
    public ResultDto<HogwartsTestUser> update(HogwartsTestUser hogwartsTestUser) {

        hogwartsTestUser.setCreateTime(new Date());
        hogwartsTestUser.setUpdateTime(new Date());
        hogwartsTestUserMapper.updateByPrimaryKeySelective(hogwartsTestUser);
        //hogwartsTestUserMapper.updateUserDemo(hogwartsTestUser.getUserName(), hogwartsTestUser.getPassword(),hogwartsTestUser.getEmail(),hogwartsTestUser.getId());
        return ResultDto.success("成功", hogwartsTestUser);
    }

    /**
     * 根据用户id或名称查询
     *
     * @param hogwartsTestUser
     * @return
     */
    @Override
    public ResultDto<List<HogwartsTestUser>> getByName(HogwartsTestUser hogwartsTestUser) {

        //List<HogwartsTestUser> hogwartsTestUserList = hogwartsTestUserMapper.getByName(hogwartsTestUser.getUserName(), hogwartsTestUser.getId());
        List<HogwartsTestUser> hogwartsTestUserList = hogwartsTestUserMapper.select(hogwartsTestUser);
        return ResultDto.success("成功", hogwartsTestUserList);
    }

    /**
     * 根据用户id删除用户信息
     *
     * @param userId
     * @return
     */
    @Override
    public ResultDto delete(Integer userId) {
        HogwartsTestUser hogwartsTestUser = new HogwartsTestUser();
        hogwartsTestUser.setId(userId);
        hogwartsTestUserMapper.delete(hogwartsTestUser);
        return ResultDto.success("成功");
    }
}
