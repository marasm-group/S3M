package org.marasm.s3m.api_implementation.queues.rabbitmq;

import lombok.AllArgsConstructor;
import lombok.Builder;
import org.marasm.s3m.api.queues.RemoteS3MQueue;

@AllArgsConstructor
@Builder
public class RabbitMqS3MQueue implements RemoteS3MQueue {

    private String name;
    private int id;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getId() {
        return id;
    }

}
