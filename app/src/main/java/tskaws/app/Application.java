package tskaws.app;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class Application extends Observable {

	// For logging purposes
	private static final String TAG = "Application";
	static Application instance = null;
	public static Application getInstance() {
		return instance;
	}

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
		String AppEventsJsonString = gson.toJson(this.getEventItems());
		return AppEventsJsonString;
	}

	public Context getContext() {
		return context;
	}

	public static Application restore(Context context) {
		Application app = new Application(context);
		//app.setEventItems(/*Restore from json*/);
		instance = app;

		Crawler crawler = new Crawler(instance);
		crawler.run();

		return instance;
	}

	public EventItem findEventById(String id) {
		for (EventItem item : getEventItems()) {
			if (item.getGuid().equals(id)) {
				return item;
			}
		}
		return null;
	}
}
