package com.hogwartsmini.demo.util;

import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

/**
 * @Author tlibn
 * @Date 2020/6/15 16:52
 **/
public class StrUtil {

    /**
     *  将存储id的list转为字符串
     *
     *  转换前=[2, 12, 22, 32]
     *  转换后= 2, 12, 22, 32
     * @param caseIdList
     * @return
     */
    public static String list2IdsStr(List<Integer> caseIdList){

        if(Objects.isNull(caseIdList)){
            return null;
        }

        return caseIdList.toString()
                .replace("[","")
                .replace("]","");

    }

    /**
     *  提取请求的baseUrl，比如http://localhost:8081/hogwartsTask/
     *
     * @param requestUrl http://localhost:8081/hogwartsTask/
     * @return http://localhost:8081/
     */
    public static String getHostAndPort(String requestUrl) {

        if(StringUtils.isEmpty(requestUrl)){
            return "";
        }

        String http = "";
        String tempUrl = "";
        //如果包含://，则截断
        if(requestUrl.contains("://")){
            http = requestUrl.substring(0,requestUrl.indexOf("://")+3);
            tempUrl = requestUrl.substring(requestUrl.indexOf("://")+3);
        }

        //如果包含/，则截断
        if(tempUrl.contains("/")){
            tempUrl = tempUrl.substring(0,tempUrl.indexOf("/"));
        }
        return http+tempUrl;
    }

    public static void main(String[] args) {
        System.out.println(getHostAndPort("http://localhost:8081/hogwartsTask/"));
    }

}
