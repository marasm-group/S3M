package org.marasm.s3m.api_implementation.queues.rabbitmq;

import com.marasm.jtdispatch.DispatchQueue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;
import org.junit.Test;
import org.marasm.s3m.api.S3MNode;
import org.marasm.s3m.api.nodes.BaseS3MNode;
import org.marasm.s3m.api.nodes.ConsumerS3MNode;
import org.marasm.s3m.api.nodes.SupplierS3MNode;
import org.marasm.s3m.api.queues.RemoteS3MQueue;
import org.marasm.s3m.api.serialization.S3MSerializer;
import org.marasm.s3m.api_implementation.ErrorMessage;
import org.marasm.s3m.api_implementation.NodeRunner;
import org.marasm.s3m.api_implementation.nodes.JavaScriptS3MNode;
import org.marasm.s3m.api_implementation.queues.MqServerConfig;
import org.marasm.s3m.api_implementation.serialization.S3MJsonSerializer;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class RabbitMqS3MQueueTest {

    private RabbitMqS3MQueue errorQueue = new RabbitMqS3MQueue("ERROR", 0, ErrorMessage.class);
    private RemoteS3MQueue queue1 = RabbitMqS3MQueue.builder().id(1).name("test_queue_1").messageClass(RabbitMqS3MQueueTest.MyDataClass.class).build();
    private RemoteS3MQueue queue2 = RabbitMqS3MQueue.builder().id(1).name("test_queue_2").messageClass(RabbitMqS3MQueueTest.MyDataClass.class).build();
    private RemoteS3MQueue queue2_js = RabbitMqS3MQueue.builder().id(1).name("test_queue_2").messageClass(HashMap.class).build();
    private RemoteS3MQueue queue3 = RabbitMqS3MQueue.builder().id(1).name("test_queue_3").messageClass(RabbitMqS3MQueueTest.MyDataClass.class).build();
    private RemoteS3MQueue queue3_js = RabbitMqS3MQueue.builder().id(1).name("test_queue_3").messageClass(HashMap.class).build();
    private MqServerConfig serverConfig = MqServerConfig.builder().host("localhost").build();
    private S3MSerializer serializer = new S3MJsonSerializer();

    @Test
    public void testQueue() throws Exception {

        DispatchQueue.get("Consumer").async(this::consumerThread);
        DispatchQueue.get("JavaProcessor").async(this::javaProcessorThread);
        DispatchQueue.get("JSProcessor").async(this::jsProcessorThread);
        DispatchQueue.get("Supplier").async(this::supplierThread);

        Thread.sleep(5000);
        RabbitMqS3MConnector queueConnector = new RabbitMqS3MConnector();
        queueConnector.init(serverConfig);
        queueConnector.connect();
        queueConnector.createQueue(queue1);
        queueConnector.createQueue(errorQueue);
        queueConnector.deleteQueue(queue2);
        queueConnector.deleteQueue(queue3);
    }

    @SneakyThrows
    private void supplierThread() {
        S3MNode supplier = new SupplierS3MNode(new Supplier<List<Serializable>>() {
            private int i = 1;

            @Override
            public List<Serializable> get() {
                return Collections.singletonList(new MyDataClass(i++, null, null));
            }
        });


        RabbitMqS3MConnector queueConnector = new RabbitMqS3MConnector();
        queueConnector.init(serverConfig);
        queueConnector.connect();
        queueConnector.createQueue(queue1);
        queueConnector.createQueue(errorQueue);
        NodeRunner.builder()
                .serializer(serializer)
                .errorQueue(errorQueue)
                .errorQueueConnector(queueConnector)
                .outputQueues(Collections.singletonList(queue1))
                .outputQueuesConnector(queueConnector)
                .node(supplier)
                .build().runLoop();
    }

    @SneakyThrows
    private void consumerThread() {
        S3MNode consumer = new ConsumerS3MNode(
                new Consumer<List<Serializable>>() {
                    long startTime = System.nanoTime();

                    @Override
                    public void accept(List<Serializable> input) {
                        RabbitMqS3MQueueTest.MyDataClass o = (RabbitMqS3MQueueTest.MyDataClass) input.get(0);
                        if (o.getId() == 1) {
                            startTime = System.nanoTime();
                        }
                        if (o.getId() % 100 == 0) {
                            BigDecimal count = new BigDecimal(o.id).setScale(10, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(1000000000));
                            final long runningTime = System.nanoTime() - startTime;
                            System.out.println(count.divide(new BigDecimal(runningTime), BigDecimal.ROUND_HALF_UP) + " messages per second");
                        }
                    }
                });


        RabbitMqS3MConnector queueConnector = new RabbitMqS3MConnector();
        queueConnector.init(serverConfig);
        queueConnector.connect();
        queueConnector.createQueue(queue3);
        queueConnector.createQueue(errorQueue);
        NodeRunner.builder()
                .serializer(serializer)
                .errorQueue(errorQueue)
                .errorQueueConnector(queueConnector)
                .inputQueues(Collections.singletonList(queue3))
                .inputQueuesConnector(queueConnector)
                .node(consumer)
                .build().runLoop();
    }

    @SneakyThrows
    private void javaProcessorThread() {
        S3MNode processor = new BaseS3MNode() {
            @Override
            public List<Serializable> process(List<Serializable> input) {
                ((MyDataClass) input.get(0)).setJavaProcessor("Java was here");
                return input;
            }
        };


        RabbitMqS3MConnector queueConnector = new RabbitMqS3MConnector();
        queueConnector.init(serverConfig);
        queueConnector.connect();
        queueConnector.createQueue(queue1);
        queueConnector.createQueue(queue2);
        queueConnector.createQueue(errorQueue);
        NodeRunner.builder()
                .serializer(serializer)
                .errorQueue(errorQueue)
                .errorQueueConnector(queueConnector)
                .inputQueues(Collections.singletonList(queue1))
                .inputQueuesConnector(queueConnector)
                .outputQueues(Collections.singletonList(queue2))
                .outputQueuesConnector(queueConnector)
                .node(processor)
                .build().runLoop();
    }

    @SneakyThrows
    private void jsProcessorThread() {
        S3MNode processor = new JavaScriptS3MNode(
                "function process(inputs) { \n" +
                        "    inputs[0].jsProcessor = 'JS was here';\n" +
                        "    return inputs;\n" +
                        "}");


        RabbitMqS3MConnector queueConnector = new RabbitMqS3MConnector();
        queueConnector.init(serverConfig);
        queueConnector.connect();
        queueConnector.createQueue(queue2_js);
        queueConnector.createQueue(queue3_js);
        queueConnector.createQueue(errorQueue);
        NodeRunner.builder()
                .serializer(serializer)
                .errorQueue(errorQueue)
                .errorQueueConnector(queueConnector)
                .inputQueues(Collections.singletonList(queue2_js))
                .inputQueuesConnector(queueConnector)
                .outputQueues(Collections.singletonList(queue3_js))
                .outputQueuesConnector(queueConnector)
                .node(processor)
                .build().runLoop();
    }

    @Data
    @AllArgsConstructor
    private class MyDataClass implements Serializable {
        private int id;
        private String javaProcessor;
        private String jsProcessor;
    }
}