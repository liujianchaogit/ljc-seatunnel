package com.ljc.seatunnel.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ljc.seatunnel.common.SeatunnelErrorEnum;
import com.ljc.seatunnel.common.SeatunnelException;
import com.ljc.seatunnel.domain.request.job.transform.*;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

import static com.ljc.seatunnel.domain.request.job.transform.Transform.FIELDMAPPER;

public class TaskOptionUtils {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static <T extends TransformOptions> T getTransformOption(
            Transform transform, String transformOptionsStr) throws IOException {
        switch (transform) {
            case FIELDMAPPER:
                return convertTransformStrToOptions(
                        transformOptionsStr, FieldMapperTransformOptions.class);
            case MULTIFIELDSPLIT:
                return convertTransformStrToOptions(
                        transformOptionsStr, SplitTransformOptions.class);
            case COPY:
                return convertTransformStrToOptions(
                        transformOptionsStr, CopyTransformOptions.class);
            case SQL:
                return convertTransformStrToOptions(transformOptionsStr, SQLTransformOptions.class);
            case FILTERROWKIND:
            case REPLACE:
            default:
                return null;
        }
    }

    public static <T extends TransformOptions> T convertTransformStrToOptions(
            String transformOptionsStr, Class<? extends TransformOptions> optionClass)
            throws IOException {
        if (StringUtils.isEmpty(transformOptionsStr)) {
            throw new SeatunnelException(
                    SeatunnelErrorEnum.ILLEGAL_STATE,
                    optionClass.getName() + " transformOptions can not be empty");
        }
        return (T) OBJECT_MAPPER.readValue(transformOptionsStr, optionClass);
    }
}
