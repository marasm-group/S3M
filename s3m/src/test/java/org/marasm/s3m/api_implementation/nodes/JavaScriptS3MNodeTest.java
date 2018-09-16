package org.marasm.s3m.api_implementation.nodes;

import org.junit.Test;

public class JavaScriptS3MNodeTest {

    @Test
    public void test() throws Exception {
        JavaScriptS3MNode javaScriptS3MNode = new JavaScriptS3MNode("function process(inputs) { return [{value: 'aValue', count : 1, number: 1234.56789}]; };");
        javaScriptS3MNode.process(null);
    }

}