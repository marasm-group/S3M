package org.marasm.s3m.api_implementation.queues.rabbitmq;

import com.marasm.jtdispatch.DispatchQueue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;
import org.junit.Test;
import org.marasm.s3m.api.S3MNode;
import org.marasm.s3m.api.nodes.ConsumerS3MNode;
import org.marasm.s3m.api.nodes.SupplierS3MNode;
import org.marasm.s3m.api.queues.RemoteS3MQueue;
import org.marasm.s3m.api.serialization.S3MSerializer;
import org.marasm.s3m.api_implementation.ErrorMessage;
import org.marasm.s3m.api_implementation.NodeRunner;
import org.marasm.s3m.api_implementation.queues.MqServerConfig;
import org.marasm.s3m.api_implementation.queues.S3MQueueConnector;
import org.marasm.s3m.api_implementation.serialization.S3MJsonSerializer;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class RabbitMqS3MQueueTest {

    private RabbitMqS3MQueue errorQueue = new RabbitMqS3MQueue("ERROR", 0, ErrorMessage.class);
    private RemoteS3MQueue queue = RabbitMqS3MQueue.builder().id(1).name("test_queue").messageClass(RabbitMqS3MQueueTest.MyDataClass.class).build();
    private MqServerConfig serverConfig = MqServerConfig.builder().host("localhost").build();
    private S3MSerializer serializer = new S3MJsonSerializer();

    @Test
    public void testQueue() throws Exception {

        DispatchQueue.get("Consumer").async(this::consumerThread);

        DispatchQueue.get("Supplier").async(this::supplierThread);

        Thread.sleep(5000);
        RabbitMqS3MConnector queueConnector = new RabbitMqS3MConnector();
        queueConnector.init(serverConfig);
        queueConnector.connect();
        queueConnector.createQueue(queue);
        queueConnector.createQueue(errorQueue);
        queueConnector.deleteQueue(queue);
        queueConnector.deleteQueue(errorQueue);
    }

    private void sendMyDataClass(S3MQueueConnector queueConnector, RemoteS3MQueue queue, S3MSerializer serializer, S3MNode source, boolean end) throws Exception {
        List<Serializable> obj = source.process(null);
        MyDataClass myDataClass = (MyDataClass) obj.get(0);
        myDataClass.setEnd(end);
        byte[] data = serializer.serialize(obj.get(0));
        queueConnector.put(queue, data);
    }

    @SneakyThrows
    private void supplierThread() {
        S3MNode supplier = new SupplierS3MNode(new Supplier<List<Serializable>>() {
            private int i = 1;

            @Override
            public List<Serializable> get() {
                return Collections.singletonList(new MyDataClass(i++, false));
            }
        });


        RabbitMqS3MConnector queueConnector = new RabbitMqS3MConnector();
        queueConnector.init(serverConfig);
        queueConnector.connect();
        queueConnector.createQueue(queue);
        queueConnector.createQueue(errorQueue);
        NodeRunner.builder()
                .serializer(serializer)
                .errorQueue(errorQueue)
                .errorQueueConnector(queueConnector)
                .outputQueues(Collections.singletonList(queue))
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
        queueConnector.createQueue(queue);
        queueConnector.createQueue(errorQueue);
        NodeRunner.builder()
                .serializer(serializer)
                .errorQueue(errorQueue)
                .errorQueueConnector(queueConnector)
                .inputQueues(Collections.singletonList(queue))
                .inputQueuesConnector(queueConnector)
                .node(consumer)
                .build().runLoop();
    }

    @Data
    @AllArgsConstructor
    class MyDataClass implements Serializable {
        int id;
        boolean end;
    }
}