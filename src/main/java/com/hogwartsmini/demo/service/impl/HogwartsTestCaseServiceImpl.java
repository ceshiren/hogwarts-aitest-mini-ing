package com.hogwartsmini.demo.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.hogwartsmini.demo.common.PageTableRequest;
import com.hogwartsmini.demo.common.PageTableResponse;
import com.hogwartsmini.demo.common.jmeter.ApiRunMode;
import com.hogwartsmini.demo.common.jmeter.JMeterVars;
import com.hogwartsmini.demo.common.jmeter.JmeterProperties;
import com.hogwartsmini.demo.common.jmeter.LocalRunner;
import com.hogwartsmini.demo.dao.HogwartsTestCaseMapper;
import com.hogwartsmini.demo.dto.QueryHogwartsTestCaseListDto;
import com.hogwartsmini.demo.dto.RunCaseDto;
import com.hogwartsmini.demo.dto.RunCaseParamsDto;
import com.hogwartsmini.demo.entity.HogwartsTestCase;
import com.hogwartsmini.demo.service.HogwartsTestCaseService;
import com.hogwartsmini.demo.util.JMeterUtil;
import com.hogwartsmini.demo.util.StreamUtil;
import lombok.extern.slf4j.Slf4j;
import com.hogwartsmini.demo.common.ResultDto;

import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.File;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class HogwartsTestCaseServiceImpl implements HogwartsTestCaseService {

    @Autowired
    private HogwartsTestCaseMapper hogwartsTestCaseMapper;

    @Resource
    private JmeterProperties jmeterProperties;

    public void init() {
        String JMETER_HOME = getJmeterHome();

        String JMETER_PROPERTIES = JMETER_HOME + "/bin/jmeter.properties";
        JMeterUtils.loadJMeterProperties(JMETER_PROPERTIES);
        JMeterUtils.setJMeterHome(JMETER_HOME);
        JMeterUtils.setLocale(LocaleContextHolder.getLocale());

    }

    /**
     *  获取jmeter配置信息
     * @return
     */
    public String getJmeterHome() {

        String home = getClass().getResource("/").getPath() + "jmeter";
        try {
            File file = new File(home);
            if (file.exists()) {
                return home;
            } else {
                return jmeterProperties.getHome();
            }
        } catch (Exception e) {
            return jmeterProperties.getHome();
        }
    }


    /**
     *
     * @param hogwartsTestCase
     * @return
     */
    @Override
    public ResultDto<HogwartsTestCase> save(HogwartsTestCase hogwartsTestCase) {

        //参数赋值
        hogwartsTestCase.setCreateTime(new Date());
        hogwartsTestCase.setUpdateTime(new Date());
        hogwartsTestCase.setDelFlag(1);

        //数据落库
        hogwartsTestCaseMapper.insertUseGeneratedKeys(hogwartsTestCase);
        //返回响应
        return ResultDto.success("成功", hogwartsTestCase);
    }

    /**
     * 删除测试用例信息
     *
     * @param caseId
     * @return createUserId
     */
    @Override
    public ResultDto<HogwartsTestCase> delete(Integer caseId,Integer createUserId) {

        HogwartsTestCase queryHogwartsTestCase = new HogwartsTestCase();

        queryHogwartsTestCase.setId(caseId);
        queryHogwartsTestCase.setCreateUserId(createUserId);
        queryHogwartsTestCase.setDelFlag(1);

        HogwartsTestCase result = hogwartsTestCaseMapper.selectOne(queryHogwartsTestCase);

        //如果为空，则提示
        if(Objects.isNull(result)){
            return ResultDto.fail("未查到测试用例信息");
        }
        result.setDelFlag(0);
        hogwartsTestCaseMapper.updateByPrimaryKey(result);

        return ResultDto.success("成功");
    }

    /**
     * 修改测试用例信息
     *
     * @param hogwartsTestCase
     * @return
     */
    @Override
    public ResultDto<HogwartsTestCase> update(HogwartsTestCase hogwartsTestCase) {

        HogwartsTestCase queryHogwartsTestCase = new HogwartsTestCase();

        queryHogwartsTestCase.setId(hogwartsTestCase.getId());
        queryHogwartsTestCase.setCreateUserId(hogwartsTestCase.getCreateUserId());
        queryHogwartsTestCase.setDelFlag(1);

        HogwartsTestCase result = hogwartsTestCaseMapper.selectOne(queryHogwartsTestCase);

        //如果为空，则提示
        if(Objects.isNull(result)){
            return ResultDto.fail("未查到测试用例信息");
        }

        hogwartsTestCase.setCreateTime(result.getCreateTime());
        hogwartsTestCase.setUpdateTime(new Date());
        hogwartsTestCase.setDelFlag(1);

        hogwartsTestCaseMapper.updateByPrimaryKey(hogwartsTestCase);

        return ResultDto.success("成功");
    }

    /**
     * 根据id查询测试用例信息
     *
     * @param jenkinsId
     * @return createUserId
     */
    @Override
    public ResultDto<HogwartsTestCase> getById(Integer jenkinsId,Integer createUserId) {

        HogwartsTestCase queryHogwartsTestCase = new HogwartsTestCase();

        queryHogwartsTestCase.setId(jenkinsId);
        queryHogwartsTestCase.setCreateUserId(createUserId);
        queryHogwartsTestCase.setDelFlag(1);

        HogwartsTestCase result = hogwartsTestCaseMapper.selectOne(queryHogwartsTestCase);

        //如果为空，则提示，也可以直接返回成功
        if(Objects.isNull(result)){
            return ResultDto.fail("未查到测试用例信息");
        }

        return ResultDto.success("成功",result);
    }

    /**
     * 查询Jenkins信息列表
     *
     * @param pageTableRequest
     * @return
     */
    @Override
    public ResultDto<PageTableResponse<HogwartsTestCase>> list(
            PageTableRequest<QueryHogwartsTestCaseListDto> pageTableRequest) {

        QueryHogwartsTestCaseListDto params = pageTableRequest.getParams();
        Integer pageNum = pageTableRequest.getPageNum();
        Integer pageSize = pageTableRequest.getPageSize();

        //总数
        Integer recordsTotal =  hogwartsTestCaseMapper.count(params);

        //分页查询数据
        List<HogwartsTestCase> hogwartsTestJenkinsList = hogwartsTestCaseMapper
                .list(params, (pageNum - 1) * pageSize, pageSize);

        PageTableResponse<HogwartsTestCase> hogwartsTestJenkinsPageTableResponse = new PageTableResponse<>();
        hogwartsTestJenkinsPageTableResponse.setRecordsTotal(recordsTotal);
        hogwartsTestJenkinsPageTableResponse.setData(hogwartsTestJenkinsList);

        return ResultDto.success("成功", hogwartsTestJenkinsPageTableResponse);
    }

    /**
     * 执行测试用例
     *
     *  允许用户录入application名称和需要替换的值(key-value数组形式)，其中key以${}形式
     *
     * @param runCaseDto
     * @return
     */
    @Override
    public ResultDto runCase3(RunCaseDto runCaseDto) throws Exception {
        //jmeter引擎实时加载jmeter属性
        init();

        //根据参数查询对应的测试用例记录
        Integer createUserId = runCaseDto.getCreateUserId();
        Integer caseId = runCaseDto.getCaseId();
        if(Objects.isNull(caseId)){
            return ResultDto.fail("用例id为空");
        }
        HogwartsTestCase queryHogwartsTestCase = new HogwartsTestCase();
        queryHogwartsTestCase.setCreateUserId(createUserId);
        queryHogwartsTestCase.setId(caseId);
        log.info("=====执行测试用例-查库入参====："+ JSONObject.toJSONString(queryHogwartsTestCase));
        HogwartsTestCase resultHogwartsTestCase = hogwartsTestCaseMapper.selectOne(queryHogwartsTestCase);
        if(Objects.isNull(resultHogwartsTestCase)){
            return ResultDto.fail("测试用例记录未查到");
        }
        //获取测试用例数据
        String caseData = resultHogwartsTestCase.getCaseData();
        if(StringUtils.isEmpty(caseData)){
            return ResultDto.fail("用例测试数据未查到");
        }
        //获取用户自定义的jmeter脚本参数和值
        List<RunCaseParamsDto> params = runCaseDto.getParams();
        //使用自定义的jmeter脚本参数和值替换jmeter脚本中的占位符，无占位符时不会替换
        caseData = parseJmeterParams(caseData, params);
        //将用例测试数据转换为输入流
        InputStream is = StreamUtil.getStrToStream(caseData);
        //将输入流转换为脚本对象
        Object scriptWrapper = SaveService.loadElement(is);
        //通过脚本对象获取测试脚本内容
        HashTree testPlan = JMeterUtil.getHashTree(scriptWrapper);
        //没有自定义脚本的话，可以不用
        //JMeterVars.addJSR223PostProcessor(testPlan);
        //是否debug模式运行
        //设置运行模式和追加自定义参数
        String debugReportId = "";
        String runMode = StringUtils.isEmpty(debugReportId) ? ApiRunMode.RUN.name() : ApiRunMode.DEBUG.name();
        String testId = resultHogwartsTestCase.getId().toString();
        //为添加后置监听器
        JMeterUtil.addBackendListener(testId, debugReportId, runMode, testPlan, runCaseDto);
        //构造LocalRunner对象
        LocalRunner runner = new LocalRunner(testPlan);
        //运行jmeter引擎
        runner.run(testId);
        return ResultDto.success("成功");
    }

    /**
     *  动态解析jmeter参数
     * @param caseData
     * @param params
     * @return
     */
    private String parseJmeterParams(String caseData, List<RunCaseParamsDto> params) {
        if(Objects.nonNull(params)){

            for (RunCaseParamsDto runCaseParamsDto:params) {

                String key = runCaseParamsDto.getKey();

                if(Objects.isNull(key)){
                    continue;
                }
                StringBuilder keyStr = new StringBuilder();
                if(!key.startsWith("${")){
                    keyStr.append("${");
                }
                keyStr.append(key);
                if(!key.endsWith("}")){
                    keyStr.append("}");
                }

                String value = runCaseParamsDto.getValue();
                caseData = caseData.replace(keyStr.toString(),value);

            }

        }
        return caseData;
    }

}
