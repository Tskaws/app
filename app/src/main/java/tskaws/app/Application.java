package tskaws.app;

import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.CalendarContract;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
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

	/**
	 * Make new application
	 * @param context ApplicationContext from activity
	 */
	public Application(Context context) {
		this.eventList = new ArrayList<>();
		this.context = context;
	}

	/**
	 * Restore application and initialize crawler
	 * @param context
	 * @return
	 */
	public static Application restore(Context context) {
		Application app = new Application(context);
		instance = app;

		Crawler crawler = new Crawler(instance);
		crawler.run();

		return instance;
	}

	/**
	 * Retrieve the main activiy context
	 * @return
	 */
	public Context getContext() {
		return context;
	}

	/**
	 * Get Event Items
	 * @return all events in the applicaiton
	 */
	public List<EventItem> getEventItems() {
		return eventList;
	}

	/**
	 * Set Event Items
	 * clears and sets the event item list
	 * also signals a change to observers
	 * @param eventItems new event items
	 */
	public void setEventItems(List<EventItem> eventItems) {
		this.eventList = eventItems;
		this.setChanged();
	}

	/**
	 * Stars event item and signals change
	 * @param item
	 * @param starred
	 */
	public void starEventItem(EventItem item, boolean starred) {
		boolean found = false;
		for(EventItem iter : eventList) {
			if (iter.getGuid() == item.getGuid()) {
				iter.setStarred(starred, false);
				found = true;
				break;
			}
		}

		if (found) {
			setChanged();
		}
	}

	/**
	 *
	 * @return
	 */
	public Intent addToCalendar(EventItem item) {
		Calendar beginTime = Calendar.getInstance();
		beginTime.setTime(item.getDate());
		Calendar endTime = Calendar.getInstance();
		beginTime.setTime(item.getDate());
		Intent intent = new Intent(Intent.ACTION_INSERT)
				.setData(CalendarContract.Events.CONTENT_URI)
				.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
				.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis())
				.putExtra(CalendarContract.Events.TITLE, item.getTitle())
				.putExtra(CalendarContract.Events.DESCRIPTION,item.getDescription())
				.putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY);
		return intent;
	}

	/**
	 * Notifies all observers that the applicaiton state has changed
	 */
	public void setChanged() {
		super.setChanged();
		notifyObservers();
	}

	/**
	 * Serializes eventItems as json
	 * @return json string
	 */
	public String toJson() {
		Gson gson = new Gson();
		String AppEventsJsonString = gson.toJson(this.getEventItems());
		return AppEventsJsonString;
	}


	/**
	 * Returns the eventItem associated with an id
	 * @param id
	 * @return
	 */
	public EventItem findEventById(String id) {
		for (EventItem item : getEventItems()) {
			if (item.getGuid().equals(id)) {
				return item;
			}
		}
		return null;
	}
}
