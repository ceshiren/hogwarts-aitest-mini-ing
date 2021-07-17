package com.hogwartsmini.demo.service;

import com.hogwartsmini.demo.common.HogwartsToken;
import com.hogwartsmini.demo.common.PageTableRequest;
import com.hogwartsmini.demo.common.PageTableResponse;
import com.hogwartsmini.demo.common.ResultDto;
import com.hogwartsmini.demo.dto.UserDto;
import com.hogwartsmini.demo.dto.jenkins.QueryHogwartsTestJenkinsListDto;
import com.hogwartsmini.demo.entity.HogwartsTestJenkins;


public interface HogwartsTestJenkinsService {

    /**
     *  新增Jenkins
     * @param hogwartsTestJenkins
     * @return
     */
    ResultDto<HogwartsTestJenkins> save(HogwartsTestJenkins hogwartsTestJenkins);

    /**
     *  分页查询Jenkins列表
     * @param pageTableRequest
     * @return
     */
    ResultDto<PageTableResponse<HogwartsTestJenkins>> list(PageTableRequest<QueryHogwartsTestJenkinsListDto> pageTableRequest);

}
