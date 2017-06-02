package tskaws.app;

import java.util.ArrayList;
import java.util.List;

public class Application {
	private List<Activity> activityList;
	public Application() {
		this.activityList = new ArrayList<>();
	}
	private EventItem eventItems = new EventItem();
	private String user;

	public EventItem getEventItems() {
		return eventItems;
	}

	public void setEventItems(EventItem eventItems) {
		this.eventItems = eventItems;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}
	public static void main(String[] args) {
		try {
			Crawler.crawl();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
