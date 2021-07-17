package com.hogwartsmini.demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.hogwartsmini.demo.common.*;
import com.hogwartsmini.demo.dto.testcase.AddHogwartsTestCaseDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Objects;

/**
 * @Author tlibn
 * @Date 2020/7/16 17:14
 **/
@Api(tags = "霍格沃兹测试学院-测试用例管理模块")
@RestController
@RequestMapping("hogwartsCase")
@Slf4j
public class HogwartsTestCaseController {

    @Autowired
    private TokenDb tokenDb;

    @ApiOperation("文件类型测试用例上传接口")
    @PostMapping("file")
    public ResultDto saveFile(HttpServletRequest request,
                              @RequestParam("caseFile") MultipartFile caseFile,
                              AddHogwartsTestCaseDto addHogwartsTestCaseDto) throws IOException {

        log.info("addHogwartsTestCaseDto=== "+ JSONObject
                .toJSONString(addHogwartsTestCaseDto));

        //从客户端请求的header中获取token，并根据token获取用户信息 -- 为什么要从这里获取？？？
        TokenDto tokenDto = tokenDb.getUserInfo(
                request.getHeader(UserBaseStr.LOGIN_TOKEN));

        if(Objects.isNull(caseFile)){
            return ResultDto.fail("文件为空");
        }

        InputStream inputStream = caseFile.getInputStream();

        String caseDataStr = IOUtils.toString(inputStream);
        inputStream.close();
        log.info("caseDataStr== " + caseDataStr);

        File file = new File("G:/data2/test23.txt");

        caseFile.transferTo(file);

        return ResultDto.success("成功",caseDataStr);
    }

}
