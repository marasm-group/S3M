package org.marasm.s3m.loader;

import lombok.SneakyThrows;
import org.marasm.s3m.api.S3MNode;
import org.marasm.s3m.api.S3MQueue;
import org.marasm.s3m.api_implementation.NodeRunner;
import org.marasm.s3m.api_implementation.queues.MqServerConfig;
import org.marasm.s3m.api_implementation.queues.rabbitmq.RabbitMqS3MConnector;
import org.marasm.s3m.api_implementation.queues.rabbitmq.RabbitMqS3MQueue;
import org.marasm.s3m.loader.application.ApplicationDescriptor;
import org.marasm.s3m.loader.application.NodeDescriptor;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ApplicationRunner {
    IdGen id = new IdGen();
    ClassResolver classResolver = new ClassResolver();

    public void run(ApplicationDescriptor app, MqServerConfig serverConfig) {
        Map<String, RabbitMqS3MQueue> queues = app.getQueues().stream()
                .map(qd -> {
                    try {
                        return RabbitMqS3MQueue.builder()
                                .id(id.next())
                                .name(qd.getName())
                                .messageClass(classResolver.get(qd))
                                .build();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }).collect(Collectors.toMap(S3MQueue::getName, q -> q));
        for (NodeDescriptor nd : app.getNodes()) {
            new Thread(() -> runNode(nd, queues, serverConfig)).start();
        }
    }

    @SneakyThrows
    private void runNode(NodeDescriptor nd, Map<String, RabbitMqS3MQueue> queues, MqServerConfig serverConfig) {
        List<S3MQueue> inQueues = nd.getIn().stream().map(queues::get).collect(Collectors.toList());
        List<S3MQueue> outQueues = nd.getOut().stream().map(queues::get).collect(Collectors.toList());
        System.out.println("in " + inQueues.stream().map(S3MQueue::getName).collect(Collectors.joining(",")) + "\n" +
                "out " + outQueues.stream().map(S3MQueue::getName).collect(Collectors.joining(",")));
        S3MQueue errorQueue = queues.get(nd.getErrorQueue());
        S3MNode node = (S3MNode) classResolver.get(nd).newInstance();
        node.init(nd.getProperties());
        RabbitMqS3MConnector queueConnector = new RabbitMqS3MConnector();
        queueConnector.init(serverConfig);
        queueConnector.connect();
        initQueues(inQueues, queueConnector);
        initQueues(outQueues, queueConnector);
        initQueues(Collections.singletonList(errorQueue), queueConnector);
        NodeRunner.builder()
                .inputQueuesConnector(queueConnector)
                .outputQueuesConnector(queueConnector)
                .errorQueueConnector(queueConnector)
                .inputQueues(inQueues)
                .outputQueues(outQueues)
                .node(node)
                .errorQueue(errorQueue)
                .build().runLoop();
    }

    private void initQueues(List<S3MQueue> queues, RabbitMqS3MConnector queueConnector) {
        queues.forEach(q -> {
            try {
                queueConnector.createQueue(q);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static class IdGen {
        private static Integer lastId = 0;

        int next() {
            synchronized (lastId) {
                return lastId++;
            }
        }
    }
}
