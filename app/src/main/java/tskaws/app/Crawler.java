package tskaws.app;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class Crawler implements Runnable {
	private static String urlString = "http://calendar.byui.edu/RSSFeeds.aspx?data=tq9cbc8b%2btuQeZGvCTEMSP%2bfv3SYIrjQ3VTAXA335bE0WtJCqYU4mp9MMtuSlz6MRZ4LbMUU%2fO4%3d";
	public Application app;

	public Crawler(Application app) {
		this.app = app;
	}

	public void run() {
		try {
			app.setEventItems(crawl());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}

	public String getFeed() throws IOException {
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
		return result.toString().replaceAll("[^\\x20-\\x7e]", "");
	}

	public List<EventItem> crawl() throws IOException, SAXException, ParserConfigurationException {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		InputSource is = new InputSource(new StringReader(getFeed()));
		Document doc = builder.parse(is);

		doc.getDocumentElement().normalize();

		NodeList nList = doc.getElementsByTagName("item");

		List<EventItem> result = new ArrayList<>();
		SimpleDateFormat parser = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss zzz");

		for (int temp = 0; temp < nList.getLength(); temp++) {

			Node nNode = nList.item(temp);

			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;

				String imageUrl = null;
				Date date = null;

				try {
					imageUrl = eElement.getElementsByTagName("enclosure").item(0).getAttributes().getNamedItem("url").getNodeValue().trim();
				} catch (Exception e) {}

				try {
					date = parser.parse(eElement.getElementsByTagName("pubDate").item(0).getTextContent().trim());
				} catch (Exception e) {
					e.printStackTrace();
				}

				EventItem event = new EventItem(
					eElement.getElementsByTagName("guid").item(0).getTextContent().trim(),
					eElement.getElementsByTagName("title").item(0).getTextContent().trim(),
					date,
					eElement.getElementsByTagName("description").item(0).getTextContent().trim(),
					eElement.getElementsByTagName("category").item(0).getTextContent().trim(),
					eElement.getElementsByTagName("link").item(0).getTextContent().trim(),
					imageUrl
				);

				result.add(event);
			}
		}

		return result;
	}
}
