package com.ljc.seatunnel.domain.request.job.transform;

import lombok.Data;

import java.util.List;

@Data
public class FieldMapperTransformOptions implements TransformOptions {

    List<ChangeOrder> changeOrders;
    List<RenameField> renameFields;
    List<DeleteField> deleteFields;
}
