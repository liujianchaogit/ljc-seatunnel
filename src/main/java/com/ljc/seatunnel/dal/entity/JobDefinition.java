package com.ljc.seatunnel.dal.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_st_job_definition")
public class JobDefinition {

    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    @TableField private String name;

    @TableField private String description;

    /** string values */
    @TableField("job_type")
    private String jobType;

    @TableField("create_user_id")
    private Integer createUserId;

    @TableField("update_user_id")
    private Integer updateUserId;

    @TableField("create_time")
    private Date createTime;

    @TableField("update_time")
    private Date updateTime;
}
