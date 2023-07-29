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

/** Save DAG task line */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_st_job_line")
public class JobLine {

    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    @TableField("version_id")
    private Long versionId;

    @TableField("input_plugin_id")
    private String inputPluginId;

    @TableField("target_plugin_id")
    private String targetPluginId;

    @TableField("create_time")
    private Date createTime;

    @TableField("update_time")
    private Date updateTime;
}
