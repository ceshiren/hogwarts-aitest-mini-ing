package com.hogwartsmini.demo.common;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 分页查询返回
 *
 */
@Data
public class PageTableResponse<T> implements Serializable {

    private static final long serialVersionUID = 620421858510718076L;

    //总数
    private Integer recordsTotal;
    //详细列表数据
    private List<T> data;

}

