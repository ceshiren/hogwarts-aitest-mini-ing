package com.hogwartsmini.demo.util;

import com.hogwartsmini.demo.dto.RunCaseDto;
import org.apache.commons.lang3.StringUtils;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.visualizers.backend.BackendListener;
import org.apache.jmeter.visualizers.backend.influxdb.InfluxdbBackendListenerClient;
import org.apache.jorphan.collections.HashTree;

import java.lang.reflect.Field;

/**
 *  JMeter工具栏
 *
 *@Author tlibn
 *@Date 2021/8/25 16:20
 **/
public class JMeterUtil {

    /**
     *  通过反射获取对象scriptWrapper的私有属性testPlan的值，值的类型为HashTree
     * @param scriptWrapper
     * @return
     * @throws Exception
     */
    public static HashTree getHashTree(Object scriptWrapper) throws Exception {
        //getDeclaredFiled 仅能获取类本身的属性成员（包括私有、共有、保护）
        //getField 仅能获取类(及其父类可以自己测试) public属性成员
        Field field = scriptWrapper.getClass().getDeclaredField("testPlan");
        //就是让我们在用反射时访问私有变量
        field.setAccessible(true);
        //取得对象的Field属性值
        return (HashTree) field.get(scriptWrapper);
    }

    /**
     *  添加后置监听器，以InfluxdbBackendListenerClient为例
     * @param testId
     * @param debugReportId
     * @param runMode
     * @param testPlan
     * @param runCaseDto
     */
    public static void addBackendListener(String testId, String debugReportId
            , String runMode, HashTree testPlan, RunCaseDto runCaseDto) {
        BackendListener backendListener = new BackendListener();
        backendListener.setName(testId);

        //组装监听器参数，具体参数值可在jmeterUI，依次点击：测试计划右键-添加-监听器-后端监听器-后端监听器实现中选择
        //org.apache.jmeter.visualizers.backend.influxdb.InfluxdbBackendListenerClient即可
        Arguments arguments = new Arguments();
        arguments.addArgument("influxdbMetricsSender", "org.apache.jmeter.visualizers.backend.influxdb.HttpMetricsSender");
        //InfluxDB服务器
        arguments.addArgument("influxdbUrl", "http://39.107.221.71:8086/write?db=jmeter");
        //InfluxDB 服务器
        arguments.addArgument("application", runCaseDto.getApplication());
        //InfluxDB 服务器
        arguments.addArgument("measurement", "jmeter");

        arguments.addArgument("summaryOnly", "false");
        arguments.addArgument("samplersRegex", ".*");
        arguments.addArgument("percentiles", "99;95;90");
        arguments.addArgument("testTitle", "Test name");
        arguments.addArgument("eventTags", "");
        //arguments.addArgument(InfluxdbBackendListenerClient.TEST_ID, testId);
        if (StringUtils.isNotBlank(runMode)) {
            arguments.addArgument("runMode", runMode);
        }
        if (StringUtils.isNotBlank(debugReportId)) {
            arguments.addArgument("debugReportId", debugReportId);
        }
        backendListener.setArguments(arguments);
        backendListener.setClassname(InfluxdbBackendListenerClient.class.getCanonicalName());
        testPlan.add(testPlan.getArray()[0], backendListener);
    }

}


