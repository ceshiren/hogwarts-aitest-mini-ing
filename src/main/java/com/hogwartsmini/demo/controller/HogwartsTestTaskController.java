package com.hogwartsmini.demo.controller;

import com.hogwartsmini.demo.common.ResultDto;
import com.hogwartsmini.demo.common.TokenDb;
import com.hogwartsmini.demo.common.TokenDto;
import com.hogwartsmini.demo.common.UserBaseStr;
import com.hogwartsmini.demo.dto.RequestInfoDto;
import com.hogwartsmini.demo.dto.task.AddHogwartsTestTaskDto;
import com.hogwartsmini.demo.dto.task.StartTestDto;
import com.hogwartsmini.demo.dto.task.TestTaskDto;
import com.hogwartsmini.demo.dto.testcase.AddHogwartsTestCaseDto;
import com.hogwartsmini.demo.entity.HogwartsTestTask;
import com.hogwartsmini.demo.service.HogwartsTestTaskService;
import com.hogwartsmini.demo.util.StrUtil;
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
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;

/**
 * @Author tlibn
 * @Date 2020/7/16 17:14
 **/
@Api(tags = "霍格沃兹测试学院-测试用例管理模块")
@RestController
@RequestMapping("hogwartsTask")
@Slf4j
public class HogwartsTestTaskController {

    @Autowired
    private HogwartsTestTaskService hogwartsTestTaskService;
    @Autowired
    private TokenDb tokenDb;

    @ApiOperation("添加测试任务接口")
    //@RequestMapping(value = "login", method = RequestMethod.POST)
    @PostMapping()
    public ResultDto saveFile(HttpServletRequest request
            , @RequestBody TestTaskDto testTaskDto) throws IOException {

        //参数校验
        List<Integer> caseIdList = testTaskDto.getCaseIdList();
        if(Objects.isNull(caseIdList) || caseIdList.size()==0){
            return ResultDto.fail("测试用例数据不能为空");
        }


        //从tokenDb中获取TokenDto
        TokenDto tokenDto = tokenDb.getUserInfo(request.getHeader(UserBaseStr.LOGIN_TOKEN));

        AddHogwartsTestTaskDto testTask = testTaskDto.getTestTask();
        if(Objects.isNull(testTask)){
            testTask = new AddHogwartsTestTaskDto();
            testTaskDto.setTestTask(testTask);
        }
        testTask.setName("系统默认");
        testTask.setCreateUserId(tokenDto.getUserId());
        testTask.setTestJenkinsId(tokenDto.getDefaultJenkinsId());

        //调用Service.save()
        return hogwartsTestTaskService.save(testTaskDto, UserBaseStr.Task_Type_One);

    }


    @ApiOperation("添加测试任务接口")
    //@RequestMapping(value = "login", method = RequestMethod.POST)
    @PostMapping("start")
    public ResultDto startTest(HttpServletRequest request
            , @RequestBody StartTestDto startTestDto) throws IOException, URISyntaxException {


        //参数校验
        if(Objects.isNull(startTestDto)){
            return ResultDto.fail("参数不能为空");
        }
        if(Objects.isNull(startTestDto.getTaskId())){
            return ResultDto.fail("任务id不能为空");
        }

        HogwartsTestTask hogwartsTestTask = new HogwartsTestTask();
        //获取当前登录用户信息
        TokenDto tokenDto = tokenDb.getUserInfo(request.getHeader(UserBaseStr.LOGIN_TOKEN));

        hogwartsTestTask.setCreateUserId(tokenDto.getUserId());
        hogwartsTestTask.setTestJenkinsId(tokenDto.getDefaultJenkinsId());
        hogwartsTestTask.setId(startTestDto.getTaskId());
        hogwartsTestTask.setTestCommand(startTestDto.getTestCommand());


        String url = request.getRequestURL().toString();
        url = StrUtil.getHostAndPort(url);
        //用于存储后端服务baseUrl、token等数据
        RequestInfoDto requestInfoDto = new RequestInfoDto();
        requestInfoDto.setBaseUrl(url);
        requestInfoDto.setRequestUrl(url);
        requestInfoDto.setToken(tokenDto.getToken());

        return hogwartsTestTaskService.startTask(tokenDto, requestInfoDto,hogwartsTestTask);

    }
}
