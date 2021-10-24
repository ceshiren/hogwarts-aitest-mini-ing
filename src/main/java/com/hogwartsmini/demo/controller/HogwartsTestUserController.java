package com.hogwartsmini.demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.hogwartsmini.demo.common.*;
import com.hogwartsmini.demo.dto.AddHogwartsTestUserDto;
import com.hogwartsmini.demo.dto.BuildDto;
import com.hogwartsmini.demo.dto.UpdateHogwartsTestUserDto;
import com.hogwartsmini.demo.dto.UserDto;
import com.hogwartsmini.demo.entity.HogwartsTestUser;
import com.hogwartsmini.demo.service.HogwartsTestUserService;
import com.hogwartsmini.demo.util.JenkinsUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

/**
 * @Author tlibn
 * @Date 2020/7/16 17:14
 **/
@Api(tags = "霍格沃兹测试学院-用户管理模块")
@RestController
@RequestMapping("user")
@Slf4j
public class HogwartsTestUserController {

    @Autowired
    private TokenDb tokenDb;

    @Autowired
    private HogwartsTestUserService hogwartsTestUserService;

    @Value("${hogwarts.key1}")
    private String hogwartsKey1;

    @ApiOperation("登录接口")
    //@RequestMapping(value = "login", method = RequestMethod.POST)
    @PostMapping("login")
    public ResultDto<HogwartsToken> login(@RequestBody UserDto userDto){

        return hogwartsTestUserService.login(userDto);

        /*if(userDto.getName().contains("error2")){
            throw new NullPointerException();
        }
        if(userDto.getName().contains("error")){
            ServiceException.throwEx("用户名中含有error");
        }

        return ResultDto.success("成功 " + result + " hogwartsKey1= "+ hogwartsKey1,userDto);*/
    }


    @ApiOperation("用户注册")
    @PostMapping("register")
    public ResultDto<HogwartsTestUser> register(@RequestBody AddHogwartsTestUserDto addHogwartsTestUserDto){

        HogwartsTestUser hogwartsTestUser = new HogwartsTestUser();
        BeanUtils.copyProperties(addHogwartsTestUserDto, hogwartsTestUser);

        if(StringUtils.isEmpty(addHogwartsTestUserDto.getUserName())){
            return ResultDto.fail("用户名不能为空");
        }
        if(StringUtils.isEmpty(addHogwartsTestUserDto.getPassword())){
            return ResultDto.fail("密码不能为空");
        }

        log.info("用户注册 请求入参 "+JSONObject.toJSONString(hogwartsTestUser));


        return hogwartsTestUserService.save(hogwartsTestUser);
    }


    @ApiOperation("用户信息修改接口")
    @PutMapping()
    public ResultDto updateUserInfo(@RequestBody UpdateHogwartsTestUserDto updateHogwartsTestUserDto){

        HogwartsTestUser hogwartsTestUser = new HogwartsTestUser();
        BeanUtils.copyProperties(updateHogwartsTestUserDto, hogwartsTestUser);

        if(StringUtils.isEmpty(updateHogwartsTestUserDto.getUserName())){
            return ResultDto.fail("用户名不能为空");
        }
        if(StringUtils.isEmpty(updateHogwartsTestUserDto.getPassword())){
            return ResultDto.fail("密码不能为空");
        }

        log.info("用户注册 请求入参 "+JSONObject.toJSONString(hogwartsTestUser));


        return hogwartsTestUserService.update(hogwartsTestUser);
    }

    @ApiOperation("根据用户id删除用户信息")
    @DeleteMapping("{userId}")
    public ResultDto delete(@PathVariable("userId") Integer userId){

        System.out.println("根据用户id删除用户信息 入参 " + userId);
        return hogwartsTestUserService.delete(userId);
    }

    @RequestMapping(value = "byId/{userId}/{id}", method = RequestMethod.GET)
    public String getById(@PathVariable("userId") Long userId, @PathVariable("id") Long id){

        System.out.println("userId" + userId);
        System.out.println("id" + id);
        return "成功  " + userId + " id= " + id;
    }

    //@RequestMapping(value = "byId", method = RequestMethod.GET)
    @GetMapping("byId")
    public String getById2(@RequestParam(value = "userId", required = false) Long userId, @RequestParam("id") Long id){

        System.out.println("RequestParam userId" + userId);
        System.out.println("RequestParam id" + id);
        return "成功 RequestParam  " + userId + " id= " + id;
    }

    //@RequestMapping(value = "byId", method = RequestMethod.GET)
    @ApiOperation("根据用户名模糊查询")
    @GetMapping("byName")
    public ResultDto<List<HogwartsTestUser>> getByName(@RequestParam(value = "userId", required = false) Integer userId, @RequestParam(value = "userName", required = false) String userName){

        HogwartsTestUser hogwartsTestUser = new HogwartsTestUser();
        hogwartsTestUser.setId(userId);
        hogwartsTestUser.setUserName(userName);

        log.info("根据用户名模糊查询 请求入参 "+JSONObject.toJSONString(hogwartsTestUser));


        return hogwartsTestUserService.getByName(hogwartsTestUser);
    }

    //@RequestMapping(value = "byId", method = RequestMethod.GET)
    @GetMapping("isLogin")
    public ResultDto isLogin(HttpServletRequest request){

        //1、从请求的Header获取客户端附加token
        String tokenStr = request.getHeader(UserConstants.LOGIN_TOKEN);

        TokenDto tokenDto = tokenDb.getUserInfo(tokenStr);

        return ResultDto.success("成功",tokenDto);
    }

    @DeleteMapping("logout")
    public ResultDto logout(HttpServletRequest request){

        //1、从请求的Header获取客户端附加token
        String tokenStr = request.getHeader(UserConstants.LOGIN_TOKEN);

        TokenDto tokenDto = tokenDb.removeUserInfo(tokenStr);

        return ResultDto.success("成功",tokenDto);
    }



    @ApiOperation("调用Jenkins构建job")
    @PutMapping("build")
    public ResultDto build(@RequestBody BuildDto buildDto) throws IOException, URISyntaxException {

        log.info("调用Jenkins构建job 请求入参 "+JSONObject.toJSONString(buildDto));
        JenkinsUtil.build(buildDto.getJobName(),buildDto.getUserId(),buildDto.getRemark(),buildDto.getTestCommand());

        return ResultDto.success("成功");
    }

}
