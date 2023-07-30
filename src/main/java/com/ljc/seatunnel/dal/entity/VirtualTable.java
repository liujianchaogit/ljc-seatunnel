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
@TableName("t_st_virtual_table")
public class VirtualTable {

    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    @TableField("datasource_id")
    private Long datasourceId;

    @TableField("virtual_database_name")
    private String virtualDatabaseName;

    @TableField("virtual_table_name")
    private String virtualTableName;

    @TableField("table_fields")
    private String tableFields;

    @TableField("description")
    private String description;

    @TableField("virtual_table_config")
    private String virtualTableConfig;

    @TableField("create_user_id")
    private Integer createUserId;

    @TableField("update_user_id")
    private Integer updateUserId;

    @TableField("create_time")
    private Date createTime;

    @TableField("update_time")
    private Date updateTime;
}
