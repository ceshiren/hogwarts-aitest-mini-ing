package com.hogwartsmini.demo.service.impl;

import com.hogwartsmini.demo.common.ResultDto;
import com.hogwartsmini.demo.dao.HogwartsTestUserMapper;
import com.hogwartsmini.demo.dto.UserDto;
import com.hogwartsmini.demo.entity.HogwartsTestUser;
import com.hogwartsmini.demo.service.HogwartsTestUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @Author tlibn
 * @Date 2020/7/17 11:03
 **/
@Service
public class HogwartsTestUserServiceImpl implements HogwartsTestUserService {

    @Autowired
    private HogwartsTestUserMapper hogwartsTestUserMapper;

    @Override
    public ResultDto<UserDto> login(UserDto userDto){

        System.out.println("userDto.getName()" + userDto.getUserName());
        System.out.println("userDto.getPwd()" + userDto.getPassword());

        HogwartsTestUser query = new HogwartsTestUser();
        query.setUserName(userDto.getUserName());
        query.setPassword(userDto.getPassword());

        HogwartsTestUser result = hogwartsTestUserMapper.selectOne(query);

        if(Objects.isNull(result)){
            return ResultDto.fail("用户名密码错误或未注册");
        }

        userDto.setToken(result.getId());
        userDto.setPassword(null);


        return ResultDto.success("成功", userDto);
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

        HogwartsTestUser query = new HogwartsTestUser();
        query.setUserName(hogwartsTestUser.getUserName());

        HogwartsTestUser result = hogwartsTestUserMapper.selectOne(query);

        if(Objects.nonNull(result)){
            return ResultDto.fail("用户名已存在");
        }

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
