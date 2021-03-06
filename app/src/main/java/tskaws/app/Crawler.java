package tskaws.app;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class Crawler {
	private static String urlString = "http://calendar.byui.edu/RSSFeeds.aspx?data=tq9cbc8b%2btuQeZGvCTEMSP%2bfv3SYIrjQ3VTAXA335bE0WtJCqYU4mp9MMtuSlz6MRZ4LbMUU%2fO4%3d";
	public Application app;

	/**
	 * Constructs a new crawler
	 * Crawler must have a refrence to app for callbacks
	 * @param app
	 */
	public Crawler(Application app) {
		this.app = app;
	}

	/**
	 * Run the crawler and it's steps
	 */
	public void run() {
		Ion.with(this.app.getContext())
                .load(Crawler.urlString)
                .asString()
                .withResponse()
                .setCallback(new FutureCallback<Response<String>>() {
                    @Override
                    public void onCompleted(Exception e, Response<String> result) {
                        if (result.getHeaders().code() == 200) {
                            try {
                                List<EventItem> eventItems = Crawler.this.parse(result.getResult().toString().replaceAll("[^\\x20-\\x7e]", ""));
								merge(eventItems);
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            } catch (SAXException e1) {
                                e1.printStackTrace();
                            } catch (ParserConfigurationException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                });
	}

	/**
	 * Call the application callback and set event items
	 * @param eventItems
	 */
	public void finish(List<EventItem> eventItems) {
		this.app.setEventItems(eventItems);
	}

	/**
	 * Merges the XML crawled eventItems with their corresponding stars in couchdb.
	 * @param eventItems
	 */
	public void merge(final List<EventItem> eventItems) {
		Ion.with(this.app.getContext())
				.load("https://db.synvox.net/student_activities/_all_docs?include_docs=true")
				.asString()
				.withResponse()
				.setCallback(new FutureCallback<Response<String>>() {
					@Override
					public void onCompleted(Exception e, Response<String> result) {
						if (result.getHeaders().code() == 200) {
							Gson gson = new Gson();
							JsonObject obj = gson.fromJson(result.getResult().toString(), JsonObject.class);
							JsonArray arr = obj.getAsJsonArray("rows");
							for(JsonElement jsonItem : arr) {
								String id = jsonItem.getAsJsonObject().get("id").getAsString();
								JsonObject doc = jsonItem.getAsJsonObject().get("doc").getAsJsonObject();
								if (doc.get("guid") == null) continue;
								String guid = doc.get("guid").getAsString();
								String rev = doc.get("_rev").getAsString();
								for (EventItem item : eventItems) {
									if (item.getGuid().equals(guid)) {
										item.addStar(new Star(id, rev));
									}
								}
							}


							finish(eventItems);
						}
					}
				});
	}

	/**
	 * Given an rss feed as a string, converts it to a list of EventItem.
	 * @param input rss feed as xml
	 * @return list of EventItem
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 */
	public List<EventItem> parse(String input) throws IOException, SAXException, ParserConfigurationException {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		InputSource is = new InputSource(new StringReader(input));
		Document doc = builder.parse(is);

		doc.getDocumentElement().normalize();

		NodeList nList = doc.getElementsByTagName("item");

		List<EventItem> existingList = this.app.getEventItems();

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

				for(EventItem item : existingList) {
					if (item.getGuid().equals(event.getGuid()) && item.isStarred()) {
						event.setStarred(true, false);
						break;
					}
				}

				result.add(event);
			}
		}

		return result;
	}


}
