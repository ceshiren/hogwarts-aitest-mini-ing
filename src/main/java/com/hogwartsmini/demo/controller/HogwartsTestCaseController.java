package com.hogwartsmini.demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.hogwartsmini.demo.common.Constants;
import com.hogwartsmini.demo.common.PageTableRequest;
import com.hogwartsmini.demo.common.PageTableResponse;
import com.hogwartsmini.demo.common.ResultDto;
import com.hogwartsmini.demo.dto.AddHogwartsTestUserDto;
import com.hogwartsmini.demo.dto.BuildDto;
import com.hogwartsmini.demo.dto.UpdateHogwartsTestUserDto;
import com.hogwartsmini.demo.dto.UserDto;
import com.hogwartsmini.demo.dto.testcase.AddHogwartsTestCaseDto;
import com.hogwartsmini.demo.dto.testcase.UpdateHogwartsTestCaseDto;
import com.hogwartsmini.demo.entity.HogwartsTestCase;
import com.hogwartsmini.demo.entity.HogwartsTestUser;
import com.hogwartsmini.demo.service.HogwartsTestCaseService;
import com.hogwartsmini.demo.service.HogwartsTestUserService;
import com.hogwartsmini.demo.util.JenkinsUtil;
import com.hogwartsmini.demo.util.StrUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @Author tlibn
 * @Date 2020/7/16 17:14
 **/
@Api(tags = "霍格沃兹测试学院-用户管理模块")
@RestController
@RequestMapping("testCase")
@Slf4j
public class HogwartsTestCaseController {

    @Autowired
    private HogwartsTestCaseService hogwartsTestCaseService;


    @ApiOperation("文件类型用例上传")
    @PostMapping("file")
    public ResultDto<HogwartsTestCase> saveFile(HttpServletRequest request
            ,@RequestParam("caseFile") MultipartFile caseFile
            , AddHogwartsTestCaseDto addHogwartsTestCaseDto) throws IOException {

        if(Objects.isNull(caseFile)){
            return ResultDto.fail("文件为空");
        }
        if(Objects.isNull(addHogwartsTestCaseDto)){
            return ResultDto.fail("参数为空");
        }
        if(StringUtils.isEmpty(addHogwartsTestCaseDto.getCaseName())){
            return ResultDto.fail("用例名称为空");
        }

        Integer userId = StrUtil.getUserId(request);

        InputStream inputStream = caseFile.getInputStream();
        String caseData = IOUtils.toString(inputStream,"UTF-8");

        HogwartsTestCase hogwartsTestCase = new HogwartsTestCase();
        hogwartsTestCase.setCaseData(caseData);
        hogwartsTestCase.setCaseName(addHogwartsTestCaseDto.getCaseName());
        hogwartsTestCase.setRemark(addHogwartsTestCaseDto.getRemark());
        hogwartsTestCase.setCreateUserId(userId);
        return hogwartsTestCaseService.save(hogwartsTestCase);
    }

    @ApiOperation("文本类型用例添加")
    @PostMapping("text")
    public ResultDto<HogwartsTestCase> saveText(HttpServletRequest request,
                                                @RequestBody AddHogwartsTestCaseDto addHogwartsTestCaseDto){

        if(Objects.isNull(addHogwartsTestCaseDto)){
            return ResultDto.fail("参数为空");
        }
        if(StringUtils.isEmpty(addHogwartsTestCaseDto.getCaseName())){
            return ResultDto.fail("用例名称为空");
        }
        if(StringUtils.isEmpty(addHogwartsTestCaseDto.getCaseData())){
            return ResultDto.fail("用例数据为空");
        }

        Integer userId = StrUtil.getUserId(request);
        HogwartsTestCase hogwartsTestCase = new HogwartsTestCase();

        BeanUtils.copyProperties(addHogwartsTestCaseDto, hogwartsTestCase);
        hogwartsTestCase.setCreateUserId(userId);
        return hogwartsTestCaseService.save(hogwartsTestCase);
    }

    @ApiOperation("文本类型用例添加")
    @PutMapping()
    public ResultDto<HogwartsTestCase> update(HttpServletRequest request,
                                                @RequestBody UpdateHogwartsTestCaseDto updateHogwartsTestCaseDto){

        if(Objects.isNull(updateHogwartsTestCaseDto)){
            return ResultDto.fail("参数为空");
        }
        if(Objects.isNull(updateHogwartsTestCaseDto.getId())){
            return ResultDto.fail("用例id为空");
        }
        if(StringUtils.isEmpty(updateHogwartsTestCaseDto.getCaseName())){
            return ResultDto.fail("用例名称为空");
        }
        if(StringUtils.isEmpty(updateHogwartsTestCaseDto.getCaseData())){
            return ResultDto.fail("用例数据为空");
        }

        Integer userId = StrUtil.getUserId(request);

        HogwartsTestCase hogwartsTestCase = new HogwartsTestCase();

        BeanUtils.copyProperties(updateHogwartsTestCaseDto, hogwartsTestCase);
        hogwartsTestCase.setCreateUserId(userId);
        return hogwartsTestCaseService.update(hogwartsTestCase);
    }


    @ApiOperation("列表查询")
    @GetMapping("list")
    public ResultDto<PageTableResponse<HogwartsTestCase>> list(HttpServletRequest request
            ,PageTableRequest pageTableRequest){


        Integer userId = StrUtil.getUserId(request);

        Map params = pageTableRequest.getParams();
        if(Objects.isNull(params)){
            params = new HashMap();
        }

        params.put("createUserId",userId);
        pageTableRequest.setParams(params);

        ResultDto<PageTableResponse<HogwartsTestCase>> responseResultDto =
                hogwartsTestCaseService.list(pageTableRequest);

        return responseResultDto;
    }

    @ApiOperation("根据id查询信息")
    @GetMapping("{caseId}")
    public ResultDto getById(@PathVariable("caseId") Integer caseId){

        System.out.println("根据id查询信息 入参 " + caseId);
        return hogwartsTestCaseService.getById(caseId);
    }

    @ApiOperation("根据id查询caseData")
    @GetMapping("data/{caseId}")
    public String getDataById(@PathVariable("caseId") Integer caseId){

        System.out.println("根据id查询caseData 入参 " + caseId);
        return hogwartsTestCaseService.getDataById(caseId);
    }


    @ApiOperation("根据id删除")
    @DeleteMapping("{caseId}")
    public ResultDto delete(@PathVariable("caseId") Integer caseId){

        System.out.println("根据id删除 入参 " + caseId);
        return hogwartsTestCaseService.delete(caseId);
    }


}
