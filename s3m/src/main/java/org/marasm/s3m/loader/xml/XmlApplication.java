package org.marasm.s3m.loader.xml;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.marasm.s3m.loader.xml.map.MapAdapter;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@XmlRootElement(name = "app")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlApplication {

    @XmlElement(name = "h")
    private Header header;
    @XmlElement(name = "b")
    private Body body;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @XmlType(propOrder = {"author", "description", "multiInstance"}, name = "h")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Header {
        private Author author;
        @Builder.Default
        @XmlAttribute(name = "mi")
        private boolean multiInstance = false;
        @Builder.Default
        @XmlAttribute(name = "desc")
        private String description = "";

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        @XmlType(propOrder = {"name", "contact"}, name = "author")
        @XmlAccessorType(XmlAccessType.FIELD)
        public static class Author {
            private String name;
            private String contact;
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @XmlType(name = "b")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Body {
        @XmlElements({
                @XmlElement(name = "q", type = Queue.class),
                @XmlElement(name = "n", type = Node.class)
        })
        private List<Element> content;

        interface Element {
        }

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        @XmlType(name = "q")
        @XmlAccessorType(XmlAccessType.FIELD)
        public static class Queue implements Element {
            private String name;
        }

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        @XmlType(name = "n")//, propOrder = {"jar", "aClass", "in", "out", "error", "properties"})
        @XmlAccessorType(XmlAccessType.FIELD)
        public static class Node implements Element {
            @XmlElement(name = "error")
            String errorQueue;
            private String jar;
            @XmlElement(name = "class")
            private String aClass;
            private List<String> in;
            private List<String> out;
            @XmlJavaTypeAdapter(MapAdapter.class)
            private Map<String, String> properties;
        }
    }
}
