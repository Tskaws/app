package com.tskaws;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Application {
	private List<Activity> activityList;
	public Application() {
		this.activityList = new ArrayList<>();
	}
	public void start() throws IOException {
		System.out.println(Crawler.getFeed());
	}
}
