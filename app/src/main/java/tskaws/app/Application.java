package tskaws.app;

import java.util.ArrayList;
import java.util.List;

public class Application {
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
}
