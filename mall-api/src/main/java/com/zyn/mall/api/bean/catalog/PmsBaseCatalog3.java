package com.zyn.mall.api.bean.catalog;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author zhaoyanan
 * @create 2019-11-08-15:41
 */
@Data
public class PmsBaseCatalog3 implements Serializable {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @Column
    private String name;

    @Column
    private String catalog2Id;

}
