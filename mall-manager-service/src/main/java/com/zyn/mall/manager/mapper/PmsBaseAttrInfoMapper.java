package com.zyn.mall.manager.mapper;

import com.zyn.mall.api.bean.base.PmsBaseAttrInfo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author zhaoyanan
 * @create 2019-11-08-19:13
 */
public interface PmsBaseAttrInfoMapper extends Mapper<PmsBaseAttrInfo> {

    List<PmsBaseAttrInfo> selectAttrInfoListByValueIdFormDb(@Param("valueIdStr") String valueIdStr);
}
