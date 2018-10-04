package org.marasm.s3m.api_implementation.queues.local;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.marasm.s3m.api.S3MQueue;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocalS3MQueue implements S3MQueue {
    private Class messageClass;
    private String name;
    private int id;
}
