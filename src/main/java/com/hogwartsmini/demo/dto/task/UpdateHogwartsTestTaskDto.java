package com.hogwartsmini.demo.dto.task;

import com.hogwartsmini.demo.entity.BaseEntityNew;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(value="修改任务对象")
@Data
public class UpdateHogwartsTestTaskDto extends BaseEntityNew {
    /**
     * ID
     */
    @ApiModelProperty(value="任务主键",required=true)
    private Integer id;

    /**
     * 名称
     */
    @ApiModelProperty(value="任务名称",required=true)
    private String name;

    /**
     * 备注
     */
    @ApiModelProperty(value="任务备注")
    private String remark;

}
