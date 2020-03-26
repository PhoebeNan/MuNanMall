package com.zyn.mall.api.bean.base;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @param
 * @return
 */
@Data
public class PmsBaseAttrValue implements Serializable {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    @Column
    private String valueName;
    @Column
    private String attrId;
    @Column
    private String isEnabled;

    @Transient
    private String urlParam;

}
