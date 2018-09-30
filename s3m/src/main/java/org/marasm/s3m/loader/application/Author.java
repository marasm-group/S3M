package org.marasm.s3m.loader.application;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class Author implements Serializable {
    private String name;
    private String contact;
}
