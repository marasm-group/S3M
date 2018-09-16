package org.marasm.s3m.api_implementation.queues;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class MqServerConfig {

    private String host;

}