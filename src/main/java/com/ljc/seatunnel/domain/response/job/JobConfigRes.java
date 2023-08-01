package com.ljc.seatunnel.domain.response.job;

import com.ljc.seatunnel.common.EngineType;
import lombok.Data;

import java.util.Map;

@Data
public class JobConfigRes {

    private long id;

    private String name;

    private String description;

    private Map<String, Object> env;

    private EngineType engine;
}
