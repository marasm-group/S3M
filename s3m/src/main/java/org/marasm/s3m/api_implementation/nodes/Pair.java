package org.marasm.s3m.api_implementation.nodes;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class Pair<K, V> {
    private K key;
    private V value;
}
