package org.marasm.s3m.loader.xml;

import lombok.SneakyThrows;
import org.marasm.s3m.loader.ApplicationLoader;
import org.marasm.s3m.loader.application.ApplicationDescriptor;
import org.marasm.s3m.loader.application.Author;
import org.marasm.s3m.loader.application.NodeDescriptor;
import org.marasm.s3m.loader.application.QueueDescriptor;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class XmlApplicationLoader implements ApplicationLoader {

    private JAXBContext jaxbContext = JAXBContext.newInstance(XmlApplication.class);

    public XmlApplicationLoader() throws JAXBException {
    }


    @SneakyThrows
    public static <T> T map(Object o, Class<T> toClass) {
        Set<Field> oFields = new HashSet<>(Arrays.asList(o.getClass().getDeclaredFields()));
        Set<Field> toFields = new HashSet<>(Arrays.asList(toClass.getDeclaredFields()));
        Set<String> toFieldsNames = toFields.stream().map(Field::getName).collect(Collectors.toSet());
        oFields = oFields.stream().filter(of -> toFieldsNames.contains(of.getName())).collect(Collectors.toSet());
        T to = toClass.newInstance();
        oFields.forEach(f -> {
            try {
                f.setAccessible(true);
                Object value = f.get(o);
                Field toF = to.getClass().getDeclaredField(f.getName());
                toF.setAccessible(true);
                toF.set(to, value);
            } catch (IllegalAccessException | NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
        });
        return to;
    }

    @Override
    public ApplicationDescriptor loadApp(InputStream input) throws JAXBException {
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        Object unmarshal = unmarshaller.unmarshal(input);
        return xmlToDescriptor((XmlApplication) unmarshal);
    }

    @Override
    public void saveApp(OutputStream out, ApplicationDescriptor ad) throws JAXBException {
        XmlApplication xmlApplication = descriptorToXml(ad);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);
        jaxbMarshaller.marshal(xmlApplication, out);
    }

    public ApplicationDescriptor xmlToDescriptor(XmlApplication a) {
        return ApplicationDescriptor.builder()
                .author(Author.builder()
                        .name(a.getHeader().getAuthor().getName())
                        .contact(a.getHeader().getAuthor().getContact())
                        .build())
                .multiInstance(a.getHeader().isMultiInstance())
                .description(a.getHeader().getDescription())
                .nodes(extractNodes(a))
                .queues(extractQueues(a))
                .build();
    }

    private List<NodeDescriptor> extractNodes(XmlApplication a) {
        return a.getBody().getContent().stream()
                .filter(o -> o instanceof XmlApplication.Body.Node)
                .map(o -> (XmlApplication.Body.Node) o)
                .map(o -> map(o, NodeDescriptor.class))
                .collect(Collectors.toList());
    }

    private List<QueueDescriptor> extractQueues(XmlApplication a) {
        return a.getBody().getContent().stream()
                .filter(o -> o instanceof XmlApplication.Body.Queue)
                .map(o -> (XmlApplication.Body.Queue) o)
                .map(o -> map(o, QueueDescriptor.class))
                .collect(Collectors.toList());
    }

    public XmlApplication descriptorToXml(ApplicationDescriptor ad) {
        return XmlApplication.builder()
                .header(buildHeader(ad))
                .body(buildBody(ad))
                .build();
    }

    private XmlApplication.Body buildBody(ApplicationDescriptor ad) {
        List<XmlApplication.Body.Queue> queues = ad.getQueues().stream().map(qd -> map(qd, XmlApplication.Body.Queue.class)).collect(Collectors.toList());
        List<XmlApplication.Body.Node> nodes = ad.getNodes().stream().map(nd -> map(nd, XmlApplication.Body.Node.class)).collect(Collectors.toList());
        List<XmlApplication.Body.Element> elements = new ArrayList<>(queues);
        elements.addAll(nodes);
        return XmlApplication.Body.builder().content(elements).build();
    }

    private XmlApplication.Header buildHeader(ApplicationDescriptor ad) {
        XmlApplication.Header.HeaderBuilder builder = XmlApplication.Header.builder();
        if (ad.getAuthor() != null) {
            builder.author(XmlApplication.Header.Author.builder()
                    .name(ad.getAuthor().getName())
                    .contact(ad.getAuthor().getContact())
                    .build());
        }
        builder.multiInstance(ad.isMultiInstance());
        return builder.build();

    }
}
