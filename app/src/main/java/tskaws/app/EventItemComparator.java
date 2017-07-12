package tskaws.app;

import java.util.Comparator;

/**
 * Created by tj on 7/10/17.
 */

public class EventItemComparator implements Comparator<EventItem> {
    @Override
    public int compare(EventItem eventItem1, EventItem eventItem2) {
        return eventItem1.getStarsCount();
    }
}
