package org.example;

import javax.xml.namespace.QName;
import javax.xml.stream.*;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.*;

public class Main {
    public static void main(String[] args) throws XMLStreamException, IOException {
        splitFunction();
    }

    public static void splitFunction() throws IOException, XMLStreamException {
        //create input
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        //create output
        XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
        //set output properties
        outputFactory.setProperty("javax.xml.stream.isRepairingNamespaces"
                , Boolean.TRUE);
        //set file path
        String xmlFile = "src/main/resources/Example.xml";
        //create event reader from factory from file
        XMLEventReader reader
                = inputFactory.createXMLEventReader(new FileReader(xmlFile));
        //show test XML
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(xmlFile))) {
            String text;
            while ((text = bufferedReader.readLine()) != null) {
                System.out.println(text);
            }
        }
        //go through XML
        int count = 0;
        QName name = new QName(null, "start");
        String outputFilePrefix="testXML";
        String outputFolder = "src/main/resources/splitXML";

        try {
            while (true) {
                XMLEvent event = reader.nextEvent();
                if (event.isStartElement()) {
                    StartElement element = event.asStartElement();
                    if (element.getName().equals(name)) {
                        writeToFile(reader, event, outputFolder,outputFilePrefix + (count++) + ".xml");
                    }
                }
                if (event.isEndDocument())
                    break;
            }
        } catch (XMLStreamException e) {
            throw e;
        } finally {
            reader.close();
        }

    }

    private static void writeToFile(XMLEventReader reader,
                                    XMLEvent startEvent, String folderName,
                                    String filename)
            throws XMLStreamException, IOException {
        XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
        //set output properties
        outputFactory.setProperty("javax.xml.stream.isRepairingNamespaces"
                , Boolean.TRUE);
        StartElement element = startEvent.asStartElement();
        QName name = element.getName();
        int stack = 1;
        XMLEventWriter writer
                = outputFactory.createXMLEventWriter( new FileWriter( new File(folderName, filename )));
        writer.add(element);
        while (true) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()
                    && event.asStartElement().getName().equals(name))
                stack++;
            if (event.isEndElement()) {
                EndElement end = event.asEndElement();
                if (end.getName().equals(name)) {
                    stack--;
                    if (stack == 0) {
                        writer.add(event);
                        break;
                    }
                }
            }
            writer.add(event);
        }
        writer.close();
    }

}