package com.ljc.seatunnel.domain.request.job;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SelectTableFields {

    private boolean all;
    private List<String> tableFields;
}
