package com.hogwartsmini.demo.dto.task;

import com.hogwartsmini.demo.common.BaseDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(value="执行测试任务类",description="请求参数类" )
@Data
public class TaskDataDto extends BaseDto {

    /**
     * 任务数量
     */
    @ApiModelProperty(value="任务数量",required=true)
    private Integer taskCount;

    /**
     * 分类的key
     */
    @ApiModelProperty(value="分类的key", required = true)
    private Integer taskKey;

    /**
     * 描述
     */
    @ApiModelProperty(value="描述")
    private String desc;

}
