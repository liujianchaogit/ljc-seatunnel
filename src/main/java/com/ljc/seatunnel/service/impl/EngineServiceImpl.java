package com.ljc.seatunnel.service.impl;

import com.ljc.seatunnel.bean.engine.EngineDataType;
import com.ljc.seatunnel.service.IEngineService;
import org.springframework.stereotype.Service;

@Service
public class EngineServiceImpl implements IEngineService {
    @Override
    public EngineDataType.DataType[] listSupportDataTypes() {
        return new EngineDataType.DataType[]{
                EngineDataType.T_STRING,
                EngineDataType.T_BOOLEAN,
                EngineDataType.T_BYTE,
                EngineDataType.T_SHORT,
                EngineDataType.T_INT,
                EngineDataType.T_LONG,
                EngineDataType.T_FLOAT,
                EngineDataType.T_DOUBLE,
                EngineDataType.T_VOID,
                EngineDataType.T_DECIMAL,
                EngineDataType.T_LOCAL_DATE,
                EngineDataType.T_LOCAL_TIME,
                EngineDataType.T_LOCAL_DATE_TIME,
                EngineDataType.T_PRIMITIVE_BYTE_ARRAY,
                EngineDataType.T_STRING_ARRAY,
                EngineDataType.T_BOOLEAN_ARRAY,
                EngineDataType.T_BYTE_ARRAY,
                EngineDataType.T_SHORT_ARRAY,
                EngineDataType.T_INT_ARRAY,
                EngineDataType.T_LONG_ARRAY,
                EngineDataType.T_FLOAT_ARRAY,
                EngineDataType.T_DOUBLE_ARRAY
        };
    }
}
