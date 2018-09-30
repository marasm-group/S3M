package org.marasm.s3m.api_implementation.nodes;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.marasm.s3m.api.nodes.BaseS3MNode;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Undefined;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
public class JavaScriptS3MNode extends BaseS3MNode {

    @Getter
    private String code;

    public JavaScriptS3MNode(String js) {
        HashMap<String, String> properties = new HashMap<>();
        properties.put("js", js);
        this.init(properties);
    }


    @Override
    @SneakyThrows
    public void init(Map<String, String> properties) {
        code = properties.get("js");
    }

    @Override
    public List<Serializable> process(List<Serializable> input) throws Exception {
        Context context = Context.enter();
        try {
            Scriptable scope = context.initStandardObjects();
            if (input != null) {
                Object wrapped = ScriptValueConverter.wrapValue(scope, input);
                ScriptableObject.putProperty(scope, "input", wrapped);
            }
            Object result = context.evaluateString(scope, code, "script", 1, null);
            if (result != null && !(result instanceof Undefined)) {
                Object o = ScriptValueConverter.unwrapValue(result);
                return (List<Serializable>) o;
            } else {
                return Collections.emptyList();
            }
        } finally {
            Context.exit();
        }
    }
}
