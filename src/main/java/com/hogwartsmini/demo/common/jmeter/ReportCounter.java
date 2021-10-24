package com.hogwartsmini.demo.common.jmeter;

import lombok.Data;

import java.util.List;

@Data
@Deprecated
public class ReportCounter {
    private int number;
    private List<String> reportIds;
}
