package org.marasm.s3m.loader.application;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@Data
@Builder
public class ApplicationDescriptor implements Serializable {

    private Author author;
    @Builder.Default
    private boolean multiInstance = false;
    @Builder.Default
    private String description = "";

    @Builder.Default
    private List<QueueDescriptor> queues = Collections.emptyList();
    @Builder.Default
    private List<NodeDescriptor> nodes = Collections.emptyList();
}
