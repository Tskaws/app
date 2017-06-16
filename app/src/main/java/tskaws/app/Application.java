package tskaws.app;

import android.content.Context;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class Application extends Observable {
	private final Context context;
	private List<EventItem> eventList;

	public Application(Context context) {
		this.eventList = new ArrayList<>();
		this.context = context;
	}

	public List<EventItem> getEventItems() {
		return eventList;
	}

	public void setEventItems(List<EventItem> eventItems) {
		this.eventList = eventItems;
		this.setChanged();
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

	public Context getContext() {
		return context;
	}

	public static Application restore(Context context) {
		Application app = new Application(context);
		//app.setEventItems(/*Restore from json*/);

		Crawler crawler = new Crawler(app);
		try {
			crawler.crawl();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return app;
	}
}
