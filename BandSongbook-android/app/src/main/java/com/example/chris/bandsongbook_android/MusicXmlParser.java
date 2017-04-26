package com.example.chris.bandsongbook_android;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.*;
import java.util.ArrayList;

import javax.xml.parsers.*;
import javax.xml.parsers.DocumentBuilderFactory;

public class MusicXmlParser {

    public static String title;
    public static NodeList parts;
    public static int numParts;
    public static int measures;
    public static int lines;
    public static int tempo;
    public static int divisions;
    public static int num;
    public static int denom;

    public static ArrayList<String> partNames;
    public static ArrayList<Node> partNodes;
    public static ArrayList<NodeList> MeasureValues;
    public static ArrayList<PartInfo> PartMeasures;

    /**
     * parses the musicXML filex
     * @param xmlString xml doc read as string
     */
    public static void parser(String xmlString) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(xmlString));
            Document doc = builder.parse(is);
            doc.getDocumentElement().normalize();
            Element root = doc.getDocumentElement();

            PartMeasures = new ArrayList<PartInfo>();
            getPartList(root);
            listParts(parts);
            sepParts(root);
            getMeasures(partNodes.get(0));
            for(int i = 0; i < partNodes.size(); i++) {
                PartInfo thisPart = getMeasureInfo(partNodes.get(i), partNames.get(i));
                PartMeasures.add(thisPart);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static PartInfo getMeasureInfo(Node part, String partNo) {
        PartInfo thisPart = null;
        NodeList fields = part.getChildNodes();
        int measure = 0;
        int time = 0;
        for(int j = 0; j < fields.getLength(); j++) {
            if(fields.item(j).getNodeType() == Node.ELEMENT_NODE) {
                if(fields.item(j).getNodeName().equals("measure")) {
                    if(measure == 0) {
                        Node first = fields.item(j);
                        NodeList attr = first.getChildNodes();
                        int attrCount = 0;
                        int chordCurrent = time;
                        boolean chordNow = false;
                        for(int i = 0; i < attr.getLength(); i++) {
                            if(attr.item(i).getNodeType() == Node.ELEMENT_NODE) {
                                if(attrCount == 0) { // attribute tag
                                    NodeList attributes = attr.item(i).getChildNodes();
                                    for(int k = 0; k < attributes.getLength(); k++) {
                                        if(attributes.item(k).getNodeType() == Node.ELEMENT_NODE) {
                                            Node current = attributes.item(k);
                                            if(current.getNodeName().equals("divisions")) {
                                                divisions = Integer.parseInt(current.getTextContent());
                                            }
                                            else if (current.getNodeName().equals("time")) {
                                                NodeList getDetails = current.getChildNodes();
                                                for(int z = 0; z < getDetails.getLength(); z++) {
                                                    if(getDetails.item(z).getNodeType() == Node.ELEMENT_NODE) {
                                                        if(getDetails.item(z).getNodeName().equals("beats")) {
                                                            num = Integer.parseInt(getDetails.item(z).getTextContent());
                                                        }
                                                        else if(getDetails.item(z).getNodeName().equals("beat-type")) {
                                                            denom = Integer.parseInt(getDetails.item(z).getTextContent());
                                                        }
                                                    }
                                                }
                                            }
                                            else if(current.getNodeName().equals("staff-details")) {
                                                NodeList getDetails = current.getChildNodes();
                                                for(int z = 0; z < getDetails.getLength(); z++) {
                                                    if(getDetails.item(z).getNodeType() == Node.ELEMENT_NODE) {
                                                        if(getDetails.item(z).getNodeName().equals("staff-lines")) {
                                                            int lines = Integer.parseInt(getDetails.item(z).getTextContent());
                                                            thisPart = new PartInfo(lines, measures, partNo);
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                else {
                                    if(attr.item(i).getNodeName().equals("direction")) {
                                        NodeList notes = attr.item(i).getChildNodes();
                                        for(int k = 0; k < notes.getLength(); k++) {
                                            if(notes.item(k).getNodeType() == Node.ELEMENT_NODE) {
                                                if(notes.item(k).getNodeName().equals("sound")) {
                                                    NamedNodeMap temp = notes.item(k).getAttributes();
                                                    tempo = Integer.parseInt(temp.getNamedItem("tempo").getTextContent());
                                                }
                                            }
                                        }
                                    }
                                    else {
                                        Measure current = filterNotes(fields.item(j).getChildNodes(), measure);
                                        thisPart.add(current);
                                    }
                                }
                                attrCount++;
                            }
                        }
                    }
                    else {
                        Node first = fields.item(j);
                        NodeList attr = first.getChildNodes();
                        Measure currentMeasure = filterNotes(attr, measure);
                        thisPart.add(currentMeasure);
                    }
                    measure++;
                }
            }
        }
        //PartInfo info = new PartInfo(lines, measures, partNo)
        return thisPart;
    }

    public static Measure filterNotes(NodeList e, int measureNumber) {
        Measure current = new Measure(measureNumber);
        int chordCurrent = 0;
        boolean chordNow = false;
        int time = 0;
        for(int i = 0; i < e.getLength(); i++) {
            if(e.item(i).getNodeType() == Node.ELEMENT_NODE) {
                NodeList notes = e.item(i).getChildNodes();
                boolean chordFound = false;
                int duration = 0;
                int string = 0, fret = 0;
                for(int k = 0; k < notes.getLength(); k++) {
                    if(notes.item(k).getNodeType() == Node.ELEMENT_NODE) {
                        if(notes.item(k).getNodeName().equals("duration")){
                            duration = Integer.parseInt(notes.item(k).getTextContent());
                        }
                        else if(notes.item(k).getNodeName().equals("notations")) {
                            NodeList item = notes.item(k).getChildNodes();
                            for(int z = 0; z < item.getLength(); z++) {
                                if(item.item(z).getNodeType() == Node.ELEMENT_NODE && item.item(z).getNodeName().equals("technical")) {
                                    NodeList nextItem = item.item(z).getChildNodes();
                                    for(int y = 0; y < nextItem.getLength(); y++ ) {
                                        if(nextItem.item(y).getNodeType() == Node.ELEMENT_NODE) {
                                            if(nextItem.item(y).getNodeName().equals("fret")) {
                                                fret = Integer.parseInt(nextItem.item(y).getTextContent());
                                            }
                                            else if (nextItem.item(y).getNodeName().equals("string")) {
                                                string = Integer.parseInt(nextItem.item(y).getTextContent());
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        else if(notes.item(k).getNodeType() == Node.ELEMENT_NODE && notes.item(k).getNodeName().equals("chord")) {
                            chordFound = true;
                        }
                    }
                }
                if(!chordNow) {
                    current.add(string, fret, time);
                    if(chordFound) {
                        chordCurrent = time;
                        chordNow = true;
                    }
                    time += duration;
                }
                else {
                    if(chordFound) {
                        current.add(string, fret, chordCurrent);
                    }
                    else {
                        current.add(string, fret, time);
                        chordNow = false;
                        time += duration;
                        chordCurrent = time;
                    }
                }
            }
        }
        return current;
    }
    /**
     * seperates the parts into measures and other info (ex lines in tab)
     * @param root root of the xml file
     */
    public static void sepParts(Element root) {

        partNodes = new ArrayList<Node>();
        NodeList children = root.getChildNodes();
        int part = 0;
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if(child.getNodeType() == Node.ELEMENT_NODE) {
                String name = child.getNodeName();
                if(name.equals("part")) {
                    partNodes.add(part, children.item(i));
                    part++;
                }
            }
        }
    }

    /**
     * gets the total number of measures in the music tab
     * @param root
     */
    public static void getMeasures(Node root) {
        NodeList fields = root.getChildNodes();
        int count = 0;
        for(int j = 0; j < fields.getLength(); j++) {
            if(fields.item(j).getNodeType() == Node.ELEMENT_NODE) {
                count++;
            }
        }
        measures = count;
    }

    /**
     * creates a list of parts and instruments to be played by those parts
     * @param x partList children
     */
    public static void listParts(NodeList x) {
        int people = 0;

        partNames = new ArrayList<String>();
        for (int i = 0; i < x.getLength(); i++) {
            Node element = x.item(i);
            if(element.getNodeType() == Node.ELEMENT_NODE) {

                NodeList children = element.getChildNodes();
                for(int j = 0; j < children.getLength();j++) {
                    if(children.item(j).getNodeType() == Node.ELEMENT_NODE) {

                        String partName;
                        if(children.item(j).getNodeName().equals("part-name")) {
                            partName = children.item(j).getTextContent();

                            while( j < children.getLength()) {
                                if(children.item(j).getNodeName().contains("score-instrument")) {
                                    NodeList instruments = children.item(j).getChildNodes();
                                    for(int k = 0; k < instruments.getLength(); k++) {
                                        if(instruments.item(k).getNodeType() ==Node.ELEMENT_NODE) {
                                            if(instruments.item(k).getNodeName().equals("instrument-name")) {
                                                String specInstr = instruments.item(k).getTextContent();
                                                partNames.add(specInstr);
                                            }
                                        }
                                    }
                                }
                                j++;
                            }
                        }
                    }
                }
                people++;
            }
        }
        numParts = people;
    }

    /**
     * locates the partList and oututs the children
     * @param root the part-list node of the xml file
     */
    public static void getPartList(Element root) {
        NodeList children = root.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if(child.getNodeType() == Node.ELEMENT_NODE) {
                String name = child.getNodeName();
                System.out.println(name);
                if(name.equals("part-list")) {
                    parts = child.getChildNodes();
                }
                else if(name.equals("work")) {
                    NodeList piece = child.getChildNodes();
                    for(int j = 0; j < piece.getLength(); j++) {
                        if(piece.item(j).getNodeType() == Node.ELEMENT_NODE && piece.item(j).getNodeName().equals("work-title")) {
                            title = piece.item(j).getTextContent();
                            System.out.println(title);
                        }
                    }
                }
            }
        }
    }
}
