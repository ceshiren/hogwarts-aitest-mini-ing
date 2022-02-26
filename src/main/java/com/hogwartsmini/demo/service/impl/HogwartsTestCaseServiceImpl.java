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
     * 列表查询
     *
     * @param request
     * @return
     */
    @Override
    public ResultDto<PageTableResponse<HogwartsTestCase>> list(PageTableRequest request) {

        Map params = request.getParams();
        Integer pageNum = request.getPageNum();
        Integer pageSize = request.getPageSize();

        //查总数
        int count = hogwartsTestCaseMapper.count(params);

        //分页数据
        List<HogwartsTestCase> hogwartsTestCaseList = hogwartsTestCaseMapper
                .list(params,(pageNum-1)*pageSize,pageSize);

        PageTableResponse<HogwartsTestCase> pageTableResponse = new PageTableResponse<>();
        pageTableResponse.setRecordsTotal(count);
        pageTableResponse.setData(hogwartsTestCaseList);


        return ResultDto.success("成功", pageTableResponse);
    }

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

        return ResultDto.success("成功",hogwartsTestCase);
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
        query.setCreateUserId(hogwartsTestCase.getCreateUserId());
        query.setId(hogwartsTestCase.getId());
        query.setDelFlag(Constants.DEL_FLAG_ONE);

        HogwartsTestCase result = hogwartsTestCaseMapper.selectOne(query);

        if(Objects.isNull(result)){
            return ResultDto.fail("未查到测试用例数据");
        }

        hogwartsTestCase.setUpdateTime(new Date());
        hogwartsTestCaseMapper.updateByPrimaryKeySelective(hogwartsTestCase);

        return ResultDto.success("成功",hogwartsTestCase);
    }

    /**
     * 根据id查询
     *
     * @param caseId
     * @return
     */
    @Override
    public ResultDto<HogwartsTestCase> getById(Integer caseId) {

        HogwartsTestCase query = new HogwartsTestCase();
        query.setId(caseId);
        query.setDelFlag(Constants.DEL_FLAG_ONE);

        HogwartsTestCase result = hogwartsTestCaseMapper.selectOne(query);

        if(Objects.isNull(result)){
            return ResultDto.fail("未查到测试用例数据");
        }

        return ResultDto.success("成功", result);
    }

    /**
     * 根据id查询caseData
     *
     * @param caseId
     * @return
     */
    @Override
    public String getDataById(Integer caseId) {
        HogwartsTestCase query = new HogwartsTestCase();
        query.setId(caseId);
        query.setDelFlag(Constants.DEL_FLAG_ONE);

        HogwartsTestCase result = hogwartsTestCaseMapper.selectOne(query);

        if(Objects.isNull(result)){
            return "无";
        }

        return result.getCaseData();


    }

    /**
     * 删除
     *
     * @param caseId
     * @return
     */
    @Override
    public ResultDto delete(Integer caseId) {

        ResultDto<HogwartsTestCase> resultDto = getById(caseId);

        if(resultDto.getResultCode()==0){
            return resultDto;
        }

        HogwartsTestCase hogwartsTestCase = resultDto.getData();
        hogwartsTestCase.setDelFlag(Constants.DEL_FLAG_ZERO);
        hogwartsTestCase.setUpdateTime(new Date());

        hogwartsTestCaseMapper.updateByPrimaryKeySelective(hogwartsTestCase);
        return ResultDto.success("成功");
    }
}
