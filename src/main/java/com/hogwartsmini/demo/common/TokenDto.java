package com.hogwartsmini.demo.common;

import lombok.Data;

/**
 * @Author tlibn
 * @Date 2020/9/12 17:08
 **/
@Data
public class TokenDto {
    //userId/userName/defaultJenkinsId/token

    private Integer userId;
    private String userName;
    private Integer defaultJenkinsId;
    private String token;
}
