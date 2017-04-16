package xmlParser;

import javax.xml.parsers.*;

import java.io.*;
import java.util.*;
import org.w3c.dom.*;

public class MusicXmlParser {

	public static NodeList parts;
	public static int numParts;
	public static int measures;
	public static int lines;
	
	public static Hashtable<String, List<String>> partNames;
	public static ArrayList<Node> partNodes;  
	
	public static void main(String[] args) {
			parser("C:\\Users\\Chris\\workspace\\xmlParser\\src\\xmlParser\\Traditional - Silent Night.xml");
	}
	
	/**
	 * parses the musicXML file
	 * @param FilePath
	 */
	public static void parser(String FilePath) {
	      try {	
		         File inputFile = new File(FilePath);
		         DocumentBuilderFactory dbFactory 
		            = DocumentBuilderFactory.newInstance();
		         DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		         Document doc = dBuilder.parse(inputFile);
		         doc.getDocumentElement().normalize();
		         Element root = doc.getDocumentElement();
		         
		         getPartList(root);
		         listParts(parts);
		         sepParts(root);
		         getMeasures(partNodes.get(0));
		      } catch (Exception e) {
		         e.printStackTrace();
		      }
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
		System.out.println(measures + " measures");
	}
	
	/**
	 * creates a list of parts and instruments to be played by those parts
	 * @param x partList children
	 */
	public static void listParts(NodeList x) {
		int people = 0;

		partNames = new Hashtable<String, List<String>>();
		for (int i = 0; i < x.getLength(); i++) {
			Node element = x.item(i);
			if(element.getNodeType() == Node.ELEMENT_NODE) {
				
				NodeList children = element.getChildNodes();
				for(int j = 0; j < children.getLength();j++) {
					if(children.item(j).getNodeType() == Node.ELEMENT_NODE) {
						
						String partName;
						if(children.item(j).getNodeName().equals("part-name")) {
							partName = children.item(j).getTextContent();
							System.out.println(partName);
						
							while( j < children.getLength()) {
								if(children.item(j).getNodeName().contains("score-instrument")) {
									NodeList instruments = children.item(j).getChildNodes();
									List<String> parts1 = new ArrayList<String>();
									for(int k = 0; k < instruments.getLength(); k++) {
										if(instruments.item(k).getNodeType() ==Node.ELEMENT_NODE) {
											if(instruments.item(k).getNodeName().equals("instrument-name")) {
												String specInstr = instruments.item(k).getTextContent();
												System.out.println(specInstr);
												parts1.add(specInstr);
											}
										}
									}
									partNames.put(partName, parts1);
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
		System.out.println("There are " + people + " parts\n");
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
				if(name.equals("part-list")) {
					parts = child.getChildNodes();
				}
			}
		}
	}



}
