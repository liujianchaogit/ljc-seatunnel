package com.ljc.seatunnel.domain.request.job;

import com.ljc.seatunnel.domain.request.connector.BusinessMode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "JobDefinition Request", description = "job info")
public class JobReq {
    @ApiModelProperty(value = "job name", required = true, dataType = "String")
    private String name;

    @ApiModelProperty(value = "job description", dataType = "String")
    private String description;

    @ApiModelProperty(value = "job type", dataType = "String")
    private BusinessMode jobType;
}
