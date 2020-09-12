package com.hogwartsmini.demo.service.impl;

import com.hogwartsmini.demo.common.*;
import com.hogwartsmini.demo.dao.HogwartsTestUserMapper;
import com.hogwartsmini.demo.dto.UserDto;
import com.hogwartsmini.demo.entity.HogwartsTestUser;
import com.hogwartsmini.demo.service.HogwartsTestUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
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
public class HogwartsTestUserServiceImpl implements HogwartsTestUserService {

    @Autowired
    private HogwartsTestUserMapper hogwartsTestUserMapper;

    @Autowired
    private TokenDb tokenDb;

    @Override
    public ResultDto<HogwartsToken> login(UserDto userDto){
        String userName = userDto.getName();
        String password = userDto.getPwd();

        //1、获取用户录入的用户名/密码并用MD5加密
        String newPwd = DigestUtils.md5DigestAsHex((UserBaseStr.md5Hex_sign + userName+password).getBytes());
        //2、根据用户名+新密码查询数据库中是否存在数据

        HogwartsTestUser queryHogwartsTestUser = new HogwartsTestUser();
        queryHogwartsTestUser.setUserName(userName);
        queryHogwartsTestUser.setPassword(newPwd);

        HogwartsTestUser resultHogwartsTestUser = hogwartsTestUserMapper.selectOne(queryHogwartsTestUser);

        //3、若不存在提示：用户不存在或密码错误

        if(Objects.isNull(resultHogwartsTestUser)){
            return ResultDto.fail("用户不存在或密码错误");
        }

        //4、若存在，则创建Token对象，生成token并将相关信息存入TokenDto
        String tokenStr = DigestUtils.md5DigestAsHex((System.currentTimeMillis() + userName+password).getBytes());

        HogwartsToken hogwartsToken = new HogwartsToken();
        hogwartsToken.setToken(tokenStr);

        TokenDto tokenDto = new TokenDto();
        tokenDto.setUserId(resultHogwartsTestUser.getId());
        tokenDto.setUserName(resultHogwartsTestUser.getUserName());
        tokenDto.setToken(tokenStr);
        tokenDto.setDefaultJenkinsId(resultHogwartsTestUser.getDefaultJenkinsId());

        tokenDb.addUserInfo(tokenStr,tokenDto);

        return ResultDto.success("成功", hogwartsToken);
    }

    /**
     * 保存
     *
     * @param hogwartsTestUser
     * @return
     */
    @Override
    public ResultDto<HogwartsTestUser> save(HogwartsTestUser hogwartsTestUser) {


        //1、校验用户名是否已经存在
        String userName = hogwartsTestUser.getUserName();

        HogwartsTestUser queryHogwartsTestUser = new HogwartsTestUser();
        queryHogwartsTestUser.setUserName(userName);
        HogwartsTestUser resultHogwartsTestUser = hogwartsTestUserMapper.selectOne(queryHogwartsTestUser);

        if(Objects.nonNull(resultHogwartsTestUser)){
            return ResultDto.fail("用户名已存在");
        }

        //2、密码MD5加密存储：
        String password = hogwartsTestUser.getPassword();
        String newPwd = DigestUtils.md5DigestAsHex((UserBaseStr.md5Hex_sign + userName+password).getBytes());

        hogwartsTestUser.setPassword(newPwd);
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
