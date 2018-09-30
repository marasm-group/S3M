package org.marasm.s3m.loader.xml.map;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.w3c.dom.Element;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MapAdapter extends XmlAdapter<MapAdapter.MapWrapper, Map<String, Object>> {
    @Override
    public MapWrapper marshal(Map<String, Object> m) throws Exception {
        MapWrapper wrapper = new MapWrapper();
        List elements = new ArrayList<>();
        for (Map.Entry<String, Object> property : m.entrySet()) {

            if (property.getValue() instanceof Map) {
                elements.add(new JAXBElement<>(new QName(getCleanLabel(property.getKey())),
                        MapWrapper.class, marshal((Map) property.getValue())));
            } else {
                elements.add(new JAXBElement<>(new QName(getCleanLabel(property.getKey())),
                        String.class, property.getValue().toString()));
            }
        }
        wrapper.elements = elements;
        return wrapper;
    }

    @Override
    public Map<String, Object> unmarshal(MapWrapper v) throws Exception {
        return v.elements.stream()
                .map(e -> new Pair<>(e.getTagName(), e.getTextContent()))
                .collect(Collectors.toMap(Pair::getKey, Pair::getValue));
    }

    // Return a XML-safe attribute.  Might want to add camel case support
    private String getCleanLabel(String attributeLabel) {
        attributeLabel = attributeLabel.replaceAll("[()]", "").replaceAll("[^\\w\\s]", "_").replaceAll(" ", "_");
        return attributeLabel;
    }

    static class MapWrapper {
        @XmlAnyElement
        List<Element> elements;
    }

    @Data
    @AllArgsConstructor
    private static class Pair<K, V> {
        private K key;
        private V value;
    }

}