package org.marasm.s3m.loader.application;

import lombok.*;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class QueueDescriptor implements Serializable {
    private String name;
    private String messageClass;
}
