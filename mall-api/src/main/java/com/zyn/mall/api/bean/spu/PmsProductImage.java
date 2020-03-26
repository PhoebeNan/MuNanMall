package com.zyn.mall.api.bean.spu;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * @param
 * @return
 */
@Data
public class PmsProductImage implements Serializable {

    @Column
    @Id
    private String id;
    @Column
    private String productId; //spu的id
    @Column
    private String imgName;
    @Column
    private String imgUrl;

}