package com.hogwartsmini.demo.controller;

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
import java.io.IOException;
import java.io.InputStream;
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

   /* @Autowired
    private HogwartsTestJenkinsService hogwartsTestJenkinsService;
*/
    @Autowired
    private TokenDb tokenDb;

    @ApiOperation("添加Jenkins接口")
    //@RequestMapping(value = "login", method = RequestMethod.POST)
    @PostMapping("file")
    public ResultDto saveFile(HttpServletRequest request
            , @RequestParam("caseFile") MultipartFile caseFile
            , AddHogwartsTestCaseDto addHogwartsTestCaseDto) throws IOException {

        //参数校验
        if(Objects.isNull(addHogwartsTestCaseDto)){
            return ResultDto.fail("参数不为空");
        }

        //获取文件流中的内容
        InputStream inputStream = caseFile.getInputStream();
        String caseData = IOUtils.toString(inputStream,"utf-8");
        log.info("caseData== "+caseData);

        return ResultDto.success("成功",
                "caseName" + addHogwartsTestCaseDto.getCaseName()
        +" == " + caseData);

    }

}
