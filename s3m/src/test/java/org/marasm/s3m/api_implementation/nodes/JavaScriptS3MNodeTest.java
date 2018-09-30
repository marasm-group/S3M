package org.marasm.s3m.api_implementation.nodes;

import org.junit.Test;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class JavaScriptS3MNodeTest {

    @Test
    public void test() throws Exception {
        JavaScriptS3MNode javaScriptS3MNode = new JavaScriptS3MNode("[{value: 'aValue', count : 1, number: 1234.56789}];");
        List<Serializable> process = javaScriptS3MNode.process(null);
        System.out.println("process = " + process);
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
        List<Serializable> result = javaScriptS3MNode.process(Collections.singletonList(new Integer(2)));
        System.out.println("result = " + result);
    }

    @Test
    public void test3() throws Exception {
        JavaScriptS3MNode javaScriptS3MNode = new JavaScriptS3MNode("[1337];");
        List<Serializable> result = javaScriptS3MNode.process(null);
        System.out.println("result = " + result);
    }

}