package org.marasm.s3m.api_implementation.nodes;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.marasm.s3m.api.nodes.BaseS3MNode;
import org.marasm.s3m.api.serialization.S3MSerializer;
import org.marasm.s3m.api_implementation.serialization.S3MJsonSerializer;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Undefined;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@NoArgsConstructor
public class JavaScriptS3MNode extends BaseS3MNode {

    @Getter
    private String code;

    public JavaScriptS3MNode(String js) {
        HashMap<String, String> properties = new HashMap<>();
        properties.put("js", js);
        this.init(properties, new S3MJsonSerializer());
    }


    @Override
    @SneakyThrows
    public void init(Map<String, String> properties, S3MSerializer serializer) {
        code = properties.get("js");
        super.init(properties, serializer);
    }

    @Override
    public List<byte[]> process(List<byte[]> rawInput) throws Exception {
        Context context = Context.enter();
        try {
            Scriptable scope = context.initStandardObjects();
            if (rawInput != null) {
                List<Serializable> input = rawInput.stream()
                        .map(serializer::<Serializable>deserialize)
                        .collect(Collectors.toList());
                Object wrapped = ScriptValueConverter.wrapValue(scope, input);
                ScriptableObject.putProperty(scope, "input", wrapped);
            }
            Object result = context.evaluateString(scope, code, "script", 1, null);
            if (result != null && !(result instanceof Undefined)) {
                Object o = ScriptValueConverter.unwrapValue(result);
                return serialize((List<Serializable>) o);
            } else {
                return Collections.emptyList();
            }
        } finally {
            Context.exit();
        }
    }

    private List<byte[]> serialize(List<Serializable> l) {
        return l.stream().map(serializer::serialize).collect(Collectors.toList());
    }

    @Override
    public String alias() {
        return "JS";
    }
}
