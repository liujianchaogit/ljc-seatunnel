package com.ljc.seatunnel.domain.response;

import lombok.Data;

import java.util.Date;

@Data
public class BaseInfo {

    private String createUserName;

    private Date createTime;

    private String updateUserName;

    private Date updateTime;
}
