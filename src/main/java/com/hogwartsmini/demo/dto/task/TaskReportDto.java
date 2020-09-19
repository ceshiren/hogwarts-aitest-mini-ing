package com.hogwartsmini.demo.dto.task;

import com.hogwartsmini.demo.common.BaseDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel(value="执行测试任务类",description="请求参数类" )
@Data
public class TaskReportDto extends BaseDto {

    /**
     * 任务总和
     */
    @ApiModelProperty(value="任务总和",required=true)
    private Integer taskSum;

    /**
     * 任务数据对象
     */
    @ApiModelProperty(value="任务数据对象", required = true)
    private List<TaskDataDto> taskDataDtoList;

}
