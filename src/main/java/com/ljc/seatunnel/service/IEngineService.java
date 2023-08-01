package com.ljc.seatunnel.service;

import com.ljc.seatunnel.bean.engine.EngineDataType;

public interface IEngineService {
    EngineDataType.DataType[] listSupportDataTypes();
}
