package com.hogwartsmini.demo.service;

import com.hogwartsmini.demo.common.HogwartsToken;
import com.hogwartsmini.demo.common.ResultDto;
import com.hogwartsmini.demo.dto.UserDto;
import com.hogwartsmini.demo.entity.HogwartsTestUser;

import java.util.List;

public interface HogwartsTestUserService {

    ResultDto<HogwartsToken> login(UserDto userDto);

    /**
     *  保存
     * @param hogwartsTestUser
     * @return
     */
    ResultDto<HogwartsTestUser> save(HogwartsTestUser hogwartsTestUser);

    /**
     *  更新
     * @param hogwartsTestUser
     * @return
     */
    ResultDto<HogwartsTestUser> update(HogwartsTestUser hogwartsTestUser);

    /**
     *  根据用户id或名称查询
     * @param hogwartsTestUser
     * @return
     */
    ResultDto<List<HogwartsTestUser>> getByName(HogwartsTestUser hogwartsTestUser);

    /**
     *  根据用户id删除用户信息
     * @param userId
     * @return
     */
    ResultDto delete(Integer userId);
}
