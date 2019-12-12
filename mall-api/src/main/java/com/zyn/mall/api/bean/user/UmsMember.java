package com.zyn.mall.api.bean.user;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

@Data
public class UmsMember implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    private String memberLevelId;
    private String username;
    private String password;
    private String phone;
    private int status;
    private String icon;
    private Date birthday;
    private String job;
    private String personalizedSignature;
    private int integration;
    private int growth;
    private int luckeyCount;
    private int historyIntegration;

    //社交登录
    private String nickname;
    private Integer gender;
    private String city;
    private Date createTime;
    private Long sourceUid;
    private Integer sourceType;
    private String accessToken;
    private String accessCode;

}
