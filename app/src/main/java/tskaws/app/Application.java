package tskaws.app;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

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
	}

	public static void main(String[] args) {
		Application main = new Application();
		try {
			main.setEventItems(Crawler.crawl());
		} catch(Exception e) {
			e.printStackTrace();
		}

		for(EventItem item : main.getEventItems()) {
			System.out.println(item.toString());
		}
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
		setChanged();
		notifyObservers();
		save();
	}

	public void save() {
		// Serialize to JSON
		// save to disk
	}

	public static Application restore() {
		//Application app = Application.restore();
	}
}
