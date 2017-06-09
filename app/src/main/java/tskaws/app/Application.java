package tskaws.app;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import javax.xml.parsers.ParserConfigurationException;

public class Application extends Observable {
	private List<EventItem> eventList;

	public Application() {
		this.eventList = new ArrayList<>();
	}

	public List<EventItem> getEventItems() {
		return eventList;
	}

	public void setEventItems(List<EventItem> eventItems) {
		this.eventList = eventItems;
		setChanged();
	}

	public void starEventItem(EventItem item, boolean starred) {
		boolean found = false;
		for(EventItem iter : eventList) {
			if (iter.getGuid() == item.getGuid()) {
				iter.setStarred(starred);
				found = true;
				break;
			}
		}

		if (found) {
			setChanged();
		}
	}

	public void setChanged() {
		super.setChanged();
		notifyObservers();
		save();
	}

	public void save() {
		// Serialize to JSON
		// save to disk
	}

	public static Application restore() {
		Application app = new Application();
		//app.setEventItems(/*Restore from json*/);
		Crawler crawler = new Crawler(app);
		Thread thread = new Thread(crawler);
		thread.start();
		return app;
	}
}
