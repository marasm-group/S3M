package org.marasm.s3m.api_implementation.nodes;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.marasm.s3m.api.nodes.BaseS3MNode;

import javax.script.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JavaScriptS3MNode extends BaseS3MNode {

    private final static ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
    private final CompiledScript script;
    private final Invocable invocable;


    public JavaScriptS3MNode(String jsCode) throws ScriptException {
        Compilable compilable = (Compilable) engine;
        invocable = (Invocable) engine;
        script = compilable.compile(jsCode);
    }

    private static Object toObject(Object jsObj) {

        if (jsObj == null) {
            return null;
        }
        if (jsObj instanceof ScriptObjectMirror) {
            ScriptObjectMirror castedJsObj = (ScriptObjectMirror) jsObj;
            if (castedJsObj.isArray()) {
                List<Object> result = new ArrayList<>(castedJsObj.keySet().size());
                for (String key : castedJsObj.keySet()) {
                    Object o = toObject(castedJsObj.get(key));
                    int index = Integer.parseInt(key);
                    while (result.size() <= index) {
                        result.add(null);
                    }
                    result.add(index, o);
                }
                return result;
            } else {
                Map<String, Object> result = new HashMap<>(castedJsObj.keySet().size());
                for (String key : castedJsObj.keySet()) {
                    Object o = toObject(castedJsObj.get(key));
                    result.put(key, o);
                }
                return result;
            }
        } else {
            return jsObj;
        }

    }

    @Override
    public List<Serializable> process(List<Serializable> input) throws Exception {
        script.eval();
        Object result = invocable.invokeFunction("process", input);
        if (result instanceof ScriptObjectMirror) {
            ScriptObjectMirror resultMirror = (ScriptObjectMirror) result;
            if (!resultMirror.isArray()) {
                onUnexpectedResultType();
            }
        } else if (result instanceof List) {
            List<Map<String, Object>> resultMirror = (List<Map<String, Object>>) result;
        } else {
            onUnexpectedResultType();
        }
        return (List<Serializable>) toObject(result);
    }

    private void onUnexpectedResultType() {
        throw new IllegalStateException("expected javascript code to return an array!");
    }
}
