package com.hogwartsmini.demo.common.jmeter;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 *  获取spring boot配置文件中关于jmeter属性配置
 */
@Component
@ConfigurationProperties(prefix = JmeterProperties.JMETER_PREFIX)
@Setter
@Getter
public class JmeterProperties {

    public static final String JMETER_PREFIX = "jmeter";

    private String image;

    private String home;

    private String heap = "-Xms1g -Xmx1g -XX:MaxMetaspaceSize=256m";
    private String gcAlgo = "-XX:+UseG1GC -XX:MaxGCPauseMillis=100 -XX:G1ReservePercent=20";
    private Report report = new Report();

    @Getter
    @Setter
    public static class Report {
        //间隔(粒度)
        private Integer granularity = 60000;
    }
}
