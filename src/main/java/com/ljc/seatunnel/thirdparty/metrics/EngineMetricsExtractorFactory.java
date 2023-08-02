package com.ljc.seatunnel.thirdparty.metrics;

import com.ljc.seatunnel.common.SeatunnelErrorEnum;
import com.ljc.seatunnel.common.SeatunnelException;
import com.ljc.seatunnel.domain.response.engine.Engine;
import com.ljc.seatunnel.thirdparty.engine.SeaTunnelEngineMetricsExtractor;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EngineMetricsExtractorFactory {

    private final Engine engine;

    public IEngineMetricsExtractor getEngineMetricsExtractor() {
        if (engine.getName().equals("SeaTunnel")) {
            return SeaTunnelEngineMetricsExtractor.getInstance();
        }

        throw new SeatunnelException(
                SeatunnelErrorEnum.UNSUPPORTED_ENGINE, engine.getName(), engine.getVersion());
    }
}
