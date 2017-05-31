package com.tskaws;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class Crawler {
	private static String urlString = "http://calendar.byui.edu/RSSFeeds.aspx?data=tq9cbc8b%2btuQeZGvCTEMSP%2bfv3SYIrjQ3VTAXA335bE0WtJCqYU4mp9MMtuSlz6MRZ4LbMUU%2fO4%3d";

	static String getFeed() throws IOException {
		StringBuilder result = new StringBuilder();
		URL url = new URL(Crawler.urlString);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String line;
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
		rd.close();
		return result.toString();
	}

	static void crawl() throws IOException, SAXException, ParserConfigurationException {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		InputSource is = new InputSource(new StringReader(getFeed()));
		Document doc = builder.parse(is);

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
	}
}
