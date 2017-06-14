package tskaws.app;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.provider.CalendarContract;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by jj_re on 5/31/2017.
 */

public class EventItem {

    // Class member variables
    private String guid;
    private String title;
    private Date date;
    private String description;
    private String category;
    private String link;
    private String imageUrl;
    private boolean isStarred;

    /*
    private Object pictures1;
    private Object pictures2;
    */

    public EventItem(){
        this.guid = "";
        this.title = "";
        this.date = null;
        this.description = "";
        this.category = "";
        this.link = "";
        this.imageUrl = "";
        this.isStarred = false;
    }

    public EventItem(String guid, String title, Date date, String description, String category, String link, String imageUrl) {
        this.guid = guid;
        this.title = title;
        this.date = date;
        this.description = description;
        this.category = category;
        this.link = link;
        this.imageUrl = imageUrl;
        this.isStarred = false;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public boolean isStarred() {
        return isStarred;
    }

    public void setStarred(boolean starred) {
        isStarred = starred;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }

    public String getLink() {
        return link;
    }
    public void setLink(String link) {
        this.link = link;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Intent addToCalendar() {
        Calendar beginTime = Calendar.getInstance();
        beginTime.setTime(this.getDate());
        Calendar endTime = Calendar.getInstance();
        beginTime.setTime(this.getDate());
        Intent intent = new Intent(Intent.ACTION_INSERT)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis())
                .putExtra(CalendarContract.Events.TITLE, this.getTitle())
                .putExtra(CalendarContract.Events.DESCRIPTION,this.getDescription());
        return intent;
    }

    @Override
    public String toString() {
        return "EventItem {" +
                "\n guid='" + guid + '\'' +
                ",\n title='" + title + '\'' +
                ",\n date='" + date + '\'' +
                ",\n description='" + description + '\'' +
                ",\n category='" + category + '\'' +
                ",\n link='" + link + '\'' +
                ",\n imageUrl='" + imageUrl + '\'' +
                "\n}";
    }

}
