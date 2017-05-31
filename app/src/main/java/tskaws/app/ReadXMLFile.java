package com.tskaws;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

// code from https://www.mkyong.com/java/how-to-read-xml-file-in-java-dom-parser/

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;

public class ReadXMLFile {

    public static void main(String argv[]) {

        try {

            File fXmlFile = new File("/home/tj/school/spr17/cs246/src/com/TJ/xml.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);

            //optional, but recommended
            //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
            doc.getDocumentElement().normalize();

            System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

            NodeList nList = doc.getElementsByTagName("item");

            System.out.println("----------------------------");

            for (int temp = 0; temp < nList.getLength(); temp++) {

                Node nNode = nList.item(temp);

                System.out.println("\nCurrent Element :" + nNode.getNodeName());

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element eElement = (Element) nNode;

                    //System.out.println("Staff id : " + eElement.getAttribute("id"));
                    System.out.println("Title : " + eElement.getElementsByTagName("title").item(0).getTextContent().trim());
                    System.out.println("Description : " + eElement.getElementsByTagName("description").item(0).getTextContent().trim());
                    System.out.println("Category : " + eElement.getElementsByTagName("category").item(0).getTextContent().trim());
                    System.out.println("Link : " + eElement.getElementsByTagName("link").item(0).getTextContent().trim());

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

