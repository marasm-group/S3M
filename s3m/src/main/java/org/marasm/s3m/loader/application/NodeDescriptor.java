package org.marasm.s3m.loader.application;

import lombok.*;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class NodeDescriptor implements Serializable {
    @Builder.Default
    private String jar = null;
    private String aClass;
    @Builder.Default
    private List<String> in = Collections.emptyList();
    @Builder.Default
    private List<String> out = Collections.emptyList();
    @Builder.Default
    private Map<String, String> properties = Collections.emptyMap();
    @Builder.Default
    private String errorQueue = "error.queue";
}
