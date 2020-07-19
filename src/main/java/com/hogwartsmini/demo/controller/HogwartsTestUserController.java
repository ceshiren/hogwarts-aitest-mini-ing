package com.hogwartsmini.demo.controller;

import com.hogwartsmini.demo.common.ResultDto;
import com.hogwartsmini.demo.common.ServiceException;
import com.hogwartsmini.demo.dto.UserDto;
import com.hogwartsmini.demo.service.HogwartsTestUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

/**
 * @Author tlibn
 * @Date 2020/7/16 17:14
 **/
@Api(tags = "霍格沃兹测试学院-用户管理模块")
@RestController
@RequestMapping("hogwartsUser")
@Slf4j
public class HogwartsTestUserController {

    @Autowired
    private HogwartsTestUserService hogwartsTestUserService;

    @Value("${hogwarts.key1}")
    private String hogwartsKey1;

    @ApiOperation("登录接口")
    //@RequestMapping(value = "login", method = RequestMethod.POST)
    @PostMapping("login")
    public ResultDto<UserDto> login(@RequestBody UserDto userDto){

        String result = hogwartsTestUserService.login(userDto);

        if(userDto.getName().contains("error2")){
            throw new NullPointerException();
        }
        if(userDto.getName().contains("error")){
            ServiceException.throwEx("用户名中含有error");
        }

        return ResultDto.success("成功 " + result + " hogwartsKey1= "+ hogwartsKey1,userDto);
    }

    @ApiOperation("修改接口")
    @PutMapping()
    public String update(@RequestBody UserDto userDto){

        if(userDto.getName().contains("error2")){
            throw new NullPointerException();
        }
        if(userDto.getName().contains("error")){
            ServiceException.throwEx("用户名中含有error");
        }
        System.out.println("userDto.getName()" + userDto.getName());
        System.out.println("userDto.getPwd()" + userDto.getPwd());
        return "成功 ";
    }
    @DeleteMapping()
    public String delete(@RequestBody UserDto userDto){

        System.out.println("userDto.getName()" + userDto.getName());
        System.out.println("userDto.getPwd()" + userDto.getPwd());
        return "成功 ";
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

}
