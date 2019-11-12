package com.zyn.mall.api.bean;



import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

@Data
public class UmsMemberReceiveAddress implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    private String memberId;
    private String  name;
    private String  phoneNumber;
    private int defaultStatus;
    private String postCode;
    private String province;
    private String city;
    private String region;
    private String detailAddress;

}

