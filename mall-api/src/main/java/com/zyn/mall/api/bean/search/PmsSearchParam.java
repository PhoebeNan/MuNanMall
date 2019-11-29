package com.zyn.mall.api.bean.search;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zhaoyanan
 * @create 2019-11-27-15:07
 */
@Data
public class PmsSearchParam implements Serializable {

    private String catalog3Id;
    private String keyword;

    //pms_base_attr_value表中的id值
    private String[] valueId;
}
