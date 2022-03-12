package com.hogwartsmini.demo.util;

import com.hogwartsmini.demo.common.Constants;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 *@Author tlibn
 *@Date 2022/2/21 14:57
 **/
public class StrUtil {

    public static Integer getUserId(HttpServletRequest request) {
        String userIdStr = request.getHeader(Constants.TOKEN);
        return getUserId(userIdStr);
    }


    public static Integer getUserId(String userIdStr) {
        Integer userId = null;
        try{
            userId = Integer.parseInt(userIdStr);
        }catch (Exception e){
            throw new RuntimeException("用户id必须为数字");
        }
        return userId;
    }

    public static String list2Ids(List<Integer> caseIdList) {
        return caseIdList.toString().replace("[","").replace("]","");
    }

    public static void main(String[] args) {

        List<Integer> caseIdList = new ArrayList<>();
        caseIdList.add(1);
        caseIdList.add(2);
        caseIdList.add(3);

        System.out.println(list2Ids(caseIdList));
    }

}
