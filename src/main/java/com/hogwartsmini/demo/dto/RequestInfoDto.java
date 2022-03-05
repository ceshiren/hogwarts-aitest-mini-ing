package com.hogwartsmini.demo.dto;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class RequestInfoDto {

    //Jenkins回调的完整接口地址
    @ApiModelProperty(value="请求的接口地址，用于拼装命令",hidden=true)
    private String  requestUrl;
    //Jenkins回调的baseUrl地址
    @ApiModelProperty(value="请求的服务器地址，用于拼装命令",hidden=true)
    private String  baseUrl;

    //token
    @ApiModelProperty(value="token信息，用于拼装命令",hidden=true)
    private String  token;

    /**
     * ID
     */
    @ApiModelProperty(value="操作类型",example = "2")
    private Integer operType;

}
