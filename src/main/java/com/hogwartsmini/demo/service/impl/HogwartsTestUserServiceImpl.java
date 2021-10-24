package com.hogwartsmini.demo.service.impl;

import com.hogwartsmini.demo.common.*;
import com.hogwartsmini.demo.dao.HogwartsTestUserMapper;
import com.hogwartsmini.demo.dto.UserDto;
import com.hogwartsmini.demo.entity.HogwartsTestUser;
import com.hogwartsmini.demo.service.HogwartsTestUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
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
    private TokenDb tokenDb;

    @Autowired
    private HogwartsTestUserMapper hogwartsTestUserMapper;

    @Override
    public ResultDto<HogwartsToken> login(UserDto userDto){

        System.out.println("userDto.getName()" + userDto.getUserName());
        System.out.println("userDto.getPwd()" + userDto.getPassword());


        //1、获取用户录入的用户名/密码并用MD5加密

        String newPwd = DigestUtils.md5DigestAsHex((UserConstants.md5Hex_sign
                + userDto.getUserName()+userDto.getPassword()).getBytes());

        //2、根据用户名+新密码查询数据库中是否存在数据
        HogwartsTestUser queryHogwartsTestUser = new HogwartsTestUser();
        queryHogwartsTestUser.setUserName(userDto.getUserName());
        queryHogwartsTestUser.setPassword(newPwd);

        HogwartsTestUser resultHogwartsTestUser = hogwartsTestUserMapper.selectOne(queryHogwartsTestUser);

        //3、若不存在提示：用户不存在或密码错误
        if(Objects.isNull(resultHogwartsTestUser)){
            return ResultDto.fail("用户不存在或密码错误");
        }

        //4、若存在，则创建Token对象，生成token并将相关信息存入TokenDto

        HogwartsToken hogwartsToken = new HogwartsToken();

        String tokenStr = DigestUtils.md5DigestAsHex((System.currentTimeMillis()
                + userDto.getUserName()+userDto.getPassword()).getBytes());
        hogwartsToken.setToken(tokenStr);

        TokenDto tokenDto = new TokenDto();

        tokenDto.setToken(tokenStr);
        tokenDto.setUserId(resultHogwartsTestUser.getId());
        tokenDto.setDefaultJenkinsId(resultHogwartsTestUser.getDefaultJenkinsId());
        tokenDto.setUserName(resultHogwartsTestUser.getUserName());

        tokenDb.addUserInfo(tokenStr, tokenDto);


        return ResultDto.success("成功",hogwartsToken);
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
        String password = hogwartsTestUser.getPassword();

        HogwartsTestUser queryHogwartsTestUser = new HogwartsTestUser();
        queryHogwartsTestUser.setUserName(userName);
        List<HogwartsTestUser> resultHogwartsTestUserList = hogwartsTestUserMapper.select(queryHogwartsTestUser);


        if(Objects.nonNull(resultHogwartsTestUserList)&& resultHogwartsTestUserList.size()>0 ){
            return ResultDto.fail("用户名已存在");
        }

        //2、密码MD5加密存储：DigestUtils.md5DigestAsHex((UserConstants.md5Hex_sign + userName+password).getBytes());


        String newPwd = DigestUtils.md5DigestAsHex((UserConstants.md5Hex_sign + userName+password).getBytes());;

        hogwartsTestUser.setPassword(newPwd);
        hogwartsTestUser.setCreateTime(new Date());
        hogwartsTestUser.setUpdateTime(new Date());
        hogwartsTestUserMapper.insertUseGeneratedKeys(hogwartsTestUser);

        hogwartsTestUser.setPassword(null);
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
    //@Async
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
