package com.ljc.seatunnel.thirdparty.transform.impl;

import com.google.auto.service.AutoService;
import com.ljc.seatunnel.common.SeatunnelErrorEnum;
import com.ljc.seatunnel.common.SeatunnelException;
import com.ljc.seatunnel.datasource.plugins.model.TableField;
import com.ljc.seatunnel.domain.request.job.TableSchemaReq;
import com.ljc.seatunnel.domain.request.job.transform.*;
import com.ljc.seatunnel.dynamicforms.FormStructure;
import com.ljc.seatunnel.thirdparty.transform.TransformConfigSwitcher;
import org.apache.seatunnel.api.configuration.util.OptionRule;
import org.apache.seatunnel.shade.com.typesafe.config.Config;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.ljc.seatunnel.thirdparty.transform.TransformConfigSwitcherUtils.getOrderedConfigForLinkedHashMap;

@AutoService(TransformConfigSwitcher.class)
public class FieldMapperTransformSwitcher implements TransformConfigSwitcher {
    @Override
    public Transform getTransform() {
        return Transform.FIELDMAPPER;
    }

    @Override
    public FormStructure getFormStructure(OptionRule transformOptionRule) {
        return null;
    }

    @Override
    public Config mergeTransformConfig(
            Config transformConfig, TransformOptions transformOption, TableSchemaReq inputSchema) {

        LinkedHashMap<String, String> fieldsMap =
                inputSchema.getFields().stream()
                        .map(TableField::getName)
                        .collect(
                                Collectors.toMap(
                                        key -> key,
                                        key -> key,
                                        (v1, v2) -> v1,
                                        LinkedHashMap::new));

        FieldMapperTransformOptions fieldMapperTransformOptions =
                (FieldMapperTransformOptions) transformOption;

        List<DeleteField> deleteFields = fieldMapperTransformOptions.getDeleteFields();
        List<RenameField> renameFields = fieldMapperTransformOptions.getRenameFields();
        List<ChangeOrder> changeOrders = fieldMapperTransformOptions.getChangeOrders();

        for (RenameField renameField : renameFields) {
            if (!fieldsMap.containsKey(renameField.getSourceFieldName())) {
                throw new SeatunnelException(
                        SeatunnelErrorEnum.ILLEGAL_STATE,
                        "FieldMapperTransformSwitcher renameFields sourceFieldName not exist");
            }
            fieldsMap.put(renameField.getSourceFieldName(), renameField.getTargetName());
        }

        for (DeleteField deleteField : deleteFields) {
            if (!fieldsMap.containsKey(deleteField.getSourceFieldName())) {
                throw new SeatunnelException(
                        SeatunnelErrorEnum.ILLEGAL_STATE,
                        "FieldMapperTransformSwitcher deleteFields sourceFieldName not exist");
            }
            fieldsMap.remove(deleteField.getSourceFieldName());
        }

        for (ChangeOrder changeOrder : changeOrders) {
            if (!fieldsMap.containsKey(changeOrder.getSourceFieldName())) {
                throw new SeatunnelException(
                        SeatunnelErrorEnum.ILLEGAL_STATE,
                        "FieldMapperTransformSwitcher changeOrders sourceFieldName not exist");
            }
            fieldsMap =
                    reorderLinkedHashMap(
                            fieldsMap, changeOrder.getSourceFieldName(), changeOrder.getIndex());
        }

        return transformConfig.withValue(
                "field_mapper", getOrderedConfigForLinkedHashMap(fieldsMap).root());
    }

    public static LinkedHashMap<String, String> reorderLinkedHashMap(
            LinkedHashMap<String, String> map, String key, int index) {
        if (map == null || !map.containsKey(key) || index < 0 || index >= map.size()) {
            return map;
        }

        LinkedHashMap<String, String> resultMap = new LinkedHashMap<>();
        int currentIndex = 0;

        // Insert the specified key at the specified index
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (currentIndex == index) {
                resultMap.put(key, map.get(key));
            }

            if (!entry.getKey().equals(key)) {
                resultMap.put(entry.getKey(), entry.getValue());
                currentIndex++;
            }
        }

        // Handle the case when the specified index is equal to the map size
        if (index == map.size() - 1) {
            resultMap.put(key, map.get(key));
        }

        return resultMap;
    }
}
