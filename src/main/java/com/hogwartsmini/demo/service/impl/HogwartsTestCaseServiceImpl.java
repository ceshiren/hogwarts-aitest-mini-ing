package com.hogwartsmini.demo.service.impl;

import com.hogwartsmini.demo.common.Constants;
import com.hogwartsmini.demo.common.PageTableRequest;
import com.hogwartsmini.demo.common.PageTableResponse;
import com.hogwartsmini.demo.common.ResultDto;
import com.hogwartsmini.demo.dao.HogwartsTestCaseMapper;
import com.hogwartsmini.demo.entity.HogwartsTestCase;
import com.hogwartsmini.demo.service.HogwartsTestCaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @Author tlibn
 * @Date 2020/7/17 11:03
 **/
@Service
public class HogwartsTestCaseServiceImpl implements HogwartsTestCaseService {


    @Autowired
    private HogwartsTestCaseMapper hogwartsTestCaseMapper;

    /**
     * 保存
     *
     * @param hogwartsTestCase
     * @return
     */
    @Override
    public ResultDto<HogwartsTestCase> save(HogwartsTestCase hogwartsTestCase) {

        hogwartsTestCase.setCreateTime(new Date());
        hogwartsTestCase.setUpdateTime(new Date());
        hogwartsTestCase.setDelFlag(Constants.DEL_FLAG_ONE);

        hogwartsTestCaseMapper.insertUseGeneratedKeys(hogwartsTestCase);
        return ResultDto.success("成功", hogwartsTestCase);
    }

    /**
     * 更新
     *
     * @param hogwartsTestCase
     * @return
     */
    @Override
    public ResultDto<HogwartsTestCase> update(HogwartsTestCase hogwartsTestCase) {

        HogwartsTestCase query = new HogwartsTestCase();
        query.setId(hogwartsTestCase.getId());
        query.setCreateUserId(hogwartsTestCase.getCreateUserId());
        query.setDelFlag(Constants.DEL_FLAG_ONE);

        HogwartsTestCase result = hogwartsTestCaseMapper.selectOne(query);
        if(Objects.isNull(result)){
            return ResultDto.fail("数据不存在");
        }

        hogwartsTestCase.setUpdateTime(new Date());
        hogwartsTestCase.setDelFlag(Constants.DEL_FLAG_ONE);

        hogwartsTestCaseMapper.updateByPrimaryKeySelective(hogwartsTestCase);

        return ResultDto.success("成功", hogwartsTestCase);
    }

    /**
     * 列表查询
     *
     * @param pageTableRequest
     * @return
     */
    @Override
    public ResultDto<PageTableResponse<HogwartsTestCase>> list(PageTableRequest pageTableRequest) {

        Map<String,Object> params = pageTableRequest.getParams();
        Integer pageNum = pageTableRequest.getPageNum();
        Integer pageSize = pageTableRequest.getPageSize();

        int total = hogwartsTestCaseMapper.count(params);
        List<HogwartsTestCase> hogwartsTestCaseList = null;

        if(total>0){
            hogwartsTestCaseList = hogwartsTestCaseMapper.list(params,
                    (pageNum-1)*pageSize,pageSize );
        }

        PageTableResponse<HogwartsTestCase> response = new PageTableResponse<>();

        response.setRecordsTotal(total);
        response.setData(hogwartsTestCaseList);

        return ResultDto.success("成功",response);
    }

    @Override
    public ResultDto<HogwartsTestCase> getById(HogwartsTestCase hogwartsTestCase) {
        HogwartsTestCase query = new HogwartsTestCase();
        query.setId(hogwartsTestCase.getId());
        query.setCreateUserId(hogwartsTestCase.getCreateUserId());
        query.setDelFlag(Constants.DEL_FLAG_ONE);

        HogwartsTestCase result = hogwartsTestCaseMapper.selectOne(query);
        if(Objects.isNull(result)){
            return ResultDto.fail("数据不存在");
        }

        return ResultDto.success("成功", result);
    }

    /**
     * 根据用例id删除
     *
     * @param hogwartsTestCase
     * @return
     */
    @Override
    public ResultDto delete(HogwartsTestCase hogwartsTestCase) {

        ResultDto resultDto = getById(hogwartsTestCase);
        if(0==resultDto.getResultCode()){
            return resultDto;
        }

        hogwartsTestCase.setDelFlag(Constants.DEL_FLAG_ZERO);
        hogwartsTestCase.setUpdateTime(new Date());
        hogwartsTestCaseMapper.updateByPrimaryKeySelective(hogwartsTestCase);
        return ResultDto.success("成功");
    }
}
