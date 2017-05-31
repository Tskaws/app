package com.tskaws;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import tskaws.app.EventItem;

public class Application {
	private List<com.tskaws.Activity> activityList;
	public Application() {
		this.activityList = new ArrayList<>();
	}
	public void start() throws IOException {
		System.out.println(com.tskaws.Crawler.getFeed());
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
}
