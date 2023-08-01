package com.ljc.seatunnel.domain.response.job;

import lombok.Data;

import java.util.Date;

@Data
public class JobDefinitionRes {
    private long id;

    private String name;

    private String description;

    private String jobType;

    private int createUserId;

    private int updateUserId;

    private String createUserName;

    private String updateUserName;

    private Date createTime;

    private Date updateTime;

    private long projectCode;

    private String projectName;
}
