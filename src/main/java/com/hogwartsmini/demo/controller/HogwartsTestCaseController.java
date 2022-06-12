package com.hogwartsmini.demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.hogwartsmini.demo.common.PageTableRequest;
import com.hogwartsmini.demo.common.PageTableResponse;
import com.hogwartsmini.demo.common.ResultDto;
import com.hogwartsmini.demo.dto.testcase.AddHogwartsTestCaseDto;
import com.hogwartsmini.demo.dto.testcase.UpdateHogwartsTestCaseDto;
import com.hogwartsmini.demo.entity.HogwartsTestCase;
import com.hogwartsmini.demo.service.HogwartsTestCaseService;
import com.hogwartsmini.demo.util.StrUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @Author tlibn
 * @Date 2020/6/12 16:48
 **/
@Slf4j
@Api(tags = "霍格沃兹测试学院-测试用例管理")
@RestController
@RequestMapping("/testCase")
public class HogwartsTestCaseController {

    @Autowired
    private HogwartsTestCaseService hogwartsTestCaseService;

    /**
     *
     * @param addHogwartsTestCaseDto
     * @return
     */
    @ApiOperation(value = "新增文本类型用例", notes="仅用于测试用户")
    @PostMapping("text")
    public ResultDto saveText(HttpServletRequest request,
                              @RequestBody AddHogwartsTestCaseDto addHogwartsTestCaseDto){

        if(Objects.isNull(addHogwartsTestCaseDto)){
            return ResultDto.fail("参数不能为空");
        }

        if(StringUtils.isEmpty(addHogwartsTestCaseDto.getCaseName())){
            return ResultDto.fail("用例名称不能为空");
        }
        if(StringUtils.isEmpty(addHogwartsTestCaseDto.getCaseData())){
            return ResultDto.fail("用例数据不能为空");
        }

        Integer userId = StrUtil.getUserId(request);

        HogwartsTestCase hogwartsTestCase = new HogwartsTestCase();
        BeanUtils.copyProperties(addHogwartsTestCaseDto, hogwartsTestCase);
        hogwartsTestCase.setCreateUserId(userId);

        log.info("=====新增文本测试用例-请求入参====："+ JSONObject.toJSONString(addHogwartsTestCaseDto));
        return hogwartsTestCaseService.save(hogwartsTestCase);

    }

    /**
     *
     * @param addHogwartsTestCaseDto
     * @return
     */
    @ApiOperation(value = "新增文件类型用例", notes="仅用于测试用户")
    @PostMapping("file")
    public ResultDto saveFile(HttpServletRequest request,
                              @RequestParam("caseFile") MultipartFile caseFile,
                              AddHogwartsTestCaseDto addHogwartsTestCaseDto) throws IOException {
        if(Objects.isNull(caseFile)){
            return ResultDto.fail("文件不能为空");
        }
        if(Objects.isNull(addHogwartsTestCaseDto)){
            return ResultDto.fail("参数不能为空");
        }

        if(StringUtils.isEmpty(addHogwartsTestCaseDto.getCaseName())){
            return ResultDto.fail("用例名称不能为空");
        }

        InputStream inputStream = caseFile.getInputStream();
        String caseData = IOUtils.toString(inputStream, "UTF-8");
        inputStream.close();

        Integer userId = StrUtil.getUserId(request);

        HogwartsTestCase hogwartsTestCase = new HogwartsTestCase();
        BeanUtils.copyProperties(addHogwartsTestCaseDto, hogwartsTestCase);
        hogwartsTestCase.setCaseData(caseData);
        hogwartsTestCase.setCreateUserId(userId);

        log.info("=====新增文件测试用例-请求入参====："+ JSONObject.toJSONString(addHogwartsTestCaseDto));
        return hogwartsTestCaseService.save(hogwartsTestCase);

    }


    /**
     *
     * @param updateHogwartsTestCaseDto
     * @return
     */
    @ApiOperation(value = "修改测试用例")
    @PutMapping
    public ResultDto<HogwartsTestCase> update(HttpServletRequest request, @RequestBody UpdateHogwartsTestCaseDto updateHogwartsTestCaseDto){


        if(Objects.isNull(updateHogwartsTestCaseDto)){
            return ResultDto.fail("参数不能为空");
        }
        if(Objects.isNull(updateHogwartsTestCaseDto.getId())){
            return ResultDto.fail("参数id不能为空");
        }
        if(StringUtils.isEmpty(updateHogwartsTestCaseDto.getCaseName())){
            return ResultDto.fail("用例名称不能为空");
        }
        if(StringUtils.isEmpty(updateHogwartsTestCaseDto.getCaseData())){
            return ResultDto.fail("用例数据不能为空");
        }
        Integer userId = StrUtil.getUserId(request);

        HogwartsTestCase hogwartsTestCase = new HogwartsTestCase();
        BeanUtils.copyProperties(updateHogwartsTestCaseDto,hogwartsTestCase);
        hogwartsTestCase.setCreateUserId(userId);

        return hogwartsTestCaseService.update(hogwartsTestCase);
    }

    /**
     *
     * @param caseId
     * @return
     */
    @ApiOperation(value = "根据caseId查询")
    @GetMapping("/{caseId}")
    public ResultDto<HogwartsTestCase> getById(HttpServletRequest request, @PathVariable Integer caseId){

        Integer userId = StrUtil.getUserId(request);

        HogwartsTestCase hogwartsTestCase = new HogwartsTestCase();
        hogwartsTestCase.setId(caseId);
        hogwartsTestCase.setCreateUserId(userId);

        return hogwartsTestCaseService.getById(hogwartsTestCase);
    }

    /**
     *
     * @param caseId
     * @return
     */
    @ApiOperation(value = "根据caseId删除")
    @DeleteMapping("/{caseId}")
    public ResultDto<HogwartsTestCase> delete(HttpServletRequest request, @PathVariable Integer caseId){

        Integer userId = StrUtil.getUserId(request);

        HogwartsTestCase hogwartsTestCase = new HogwartsTestCase();
        hogwartsTestCase.setId(caseId);
        hogwartsTestCase.setCreateUserId(userId);

        return hogwartsTestCaseService.delete(hogwartsTestCase);
    }

    /**
     *
     * @param pageTableRequest
     * @return
     */
    @ApiOperation(value = "列表查询")
    @GetMapping("/list")
    public ResultDto<PageTableResponse<HogwartsTestCase>> list(HttpServletRequest request, PageTableRequest pageTableRequest){

        log.info("列表查询 入参  " + JSONObject.toJSONString(pageTableRequest));

        Integer userId = StrUtil.getUserId(request);

        if(Objects.isNull(pageTableRequest)){
            return ResultDto.fail("列表查询参数为空");
        }

        Map<String,Object> params = pageTableRequest.getParams();
        if(Objects.isNull(params)){
            params = new HashMap<>();
        }
        params.put("createUserId", userId);

        return hogwartsTestCaseService.list(pageTableRequest);

    }

    /**
     * 根据caseId查询case原始数据
     *  地址不要随便改 ${caseDataUrl}/testcase/data/  有引用
     * @param caseId 测试用例id
     * @return
     */
    @ApiOperation(value = "根据测试用例id查询")
    @GetMapping("data/{caseId}")
    public String getCaseDataById(HttpServletRequest request, @PathVariable Integer caseId) {
        Integer userId = StrUtil.getUserId(request);

        HogwartsTestCase hogwartsTestCase = new HogwartsTestCase();
        hogwartsTestCase.setId(caseId);
        hogwartsTestCase.setCreateUserId(userId);

        ResultDto<HogwartsTestCase>  resultDto = hogwartsTestCaseService.getById(hogwartsTestCase);

        if(0==resultDto.getResultCode()){
            return  "数据不存在";
        }
        HogwartsTestCase result = resultDto.getData();
        return result.getCaseData();
    }


}
