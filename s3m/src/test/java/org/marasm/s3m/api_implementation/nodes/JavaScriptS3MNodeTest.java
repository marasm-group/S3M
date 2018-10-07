package org.marasm.s3m.api_implementation.nodes;

import org.junit.Test;
import org.marasm.s3m.api.serialization.S3MSerializer;
import org.marasm.s3m.api_implementation.serialization.S3MJsonSerializer;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class JavaScriptS3MNodeTest {

    private S3MSerializer serializer = new S3MJsonSerializer();

    @Test
    public void test() throws Exception {
        JavaScriptS3MNode javaScriptS3MNode = new JavaScriptS3MNode("[{value: 'aValue', count : 1, number: 1234.56789}];");
        List<byte[]> process = javaScriptS3MNode.process(null);
        System.out.println("process = " + stringValue(process));
    }

    private String stringValue(List<byte[]> process) {
        return process.stream().map(String::new).collect(Collectors.joining("\n"));
    }

    @Test
    public void test2() throws Exception {
        JavaScriptS3MNode javaScriptS3MNode = new JavaScriptS3MNode("function process (input) {\n" +
                "    var result = {\n" +
                "        number: input[0],\n" +
                "        current: Math.floor(Math.sqrt(input[0]))\n" +
                "     };\n" +
                "    return [result];\n" +
                "}\n" +
                "process(input);");
        List<Integer> ts = Collections.singletonList(2);
        List<byte[]> result = javaScriptS3MNode.process(ts.stream().map(serializer::serialize).collect(Collectors.toList()));
        System.out.println("result = " + stringValue(result));
    }

    @Test
    public void test3() throws Exception {
        JavaScriptS3MNode javaScriptS3MNode = new JavaScriptS3MNode("[1337];");
        List<byte[]> result = javaScriptS3MNode.process(null);
        System.out.println("result = " + stringValue(result));
    }

}