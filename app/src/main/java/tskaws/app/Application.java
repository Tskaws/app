package tskaws.app;

import com.google.gson.Gson;

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
	}

	public String sendAppToJson() {
		// Serialize to JSON
		Gson gson = new Gson();
		String AppJsonString = gson.toJson(this);
		return AppJsonString;
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
