package com.ljc.seatunnel.dal.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.seatunnel.common.constants.JobMode;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_st_job_version")
public class JobVersion {

    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    @TableField private String name;

    @TableField("job_id")
    private Long jobId;

    /** {@link JobMode} value */
    @TableField("job_mode")
    private String jobMode;

    @TableField private String env;

    @TableField("engine_name")
    private String engineName;

    @TableField("engine_version")
    private String engineVersion;

    @TableField("create_user_id")
    private Integer createUserId;

    @TableField("update_user_id")
    private Integer updateUserId;

    @TableField("create_time")
    private Date createTime;

    @TableField("update_time")
    private Date updateTime;
}
