package tskaws.app;

import android.content.Intent;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class EventItem implements Serializable, Comparable<tskaws.app.EventItem> {
    // Class member variables
    private String guid;
    private String title;
    private Date date;
    private String description;
    private String category;
    private String link;
    private String imageUrl;
    private boolean isStarred;
    //private int totalStars;
    private List<Star> stars = new ArrayList<Star>();

    /**
     * Construct empty event item with defaults
     */
    public EventItem(){
        this.guid = "";
        this.title = "Untitled Event";
        this.date = null;
        this.description = "";
        this.category = "";
        this.link = "";
        this.imageUrl = "";
        this.isStarred = false;
//        this.totalStars = 0;
    }

    /**
     * Constructs event item with all options open.
     * @param guid
     * @param title
     * @param date
     * @param description
     * @param category
     * @param link
     * @param imageUrl
     */
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


    public String getGuid()          { return guid;      }
    public void setGuid(String guid) { this.guid = guid; }

    public boolean isStarred() { return isStarred;    }

    /**
     * Setter for isStarred
     * @todo this could be refactored to the applicaiton star item method.
     * @param starred
     * @param sendToServer
     */
    public void setStarred(boolean starred, boolean sendToServer) {
        if (starred == this.isStarred) return;

        if (sendToServer) {
            if (starred) {
                JsonObject jsonObject = new JsonObject();
                String guid = this.getGuid();
                jsonObject.addProperty("guid", guid);
                jsonObject.addProperty("title", this.getTitle());

                Ion.with(Application.getInstance().getContext())
                        .load("https://tmcd.cloudant.com/student_activities/")
                        .setJsonObjectBody(jsonObject)
                        .asJsonObject()
                        .setCallback(new FutureCallback<JsonObject>() {
                            @Override
                            public void onCompleted(Exception e, JsonObject result) {
                                if (e != null) {
                                    Log.e("Error in", "posting to database");
                                    return;
                                }
                                String id = result.get("id").getAsString();
                                String rev = result.get("rev").getAsString();
                                Star star = new Star(id, rev);
                                EventItem.this.stars.add(star);
                            }
                        });
            } else {
                JsonObject jsonObject = new JsonObject();
                String guid = this.getGuid();

                if (EventItem.this.stars.size() == 0)
                    return;

                Star star = this.stars.get(0);
                jsonObject.addProperty("_id", star.getId());
                jsonObject.addProperty("_rev", star.getRev());
                jsonObject.addProperty("_deleted", true);

                Ion.with(Application.getInstance().getContext())
                        .load("PUT", "https://tmcd.cloudant.com/student_activities/" + star.getId())
                        .setJsonObjectBody(jsonObject)
                        .asJsonObject()
                        .setCallback(new FutureCallback<JsonObject>() {
                            @Override
                            public void onCompleted(Exception e, JsonObject result) {
                                if (e != null) {
                                    Log.e("Error in", "posting to database");
                                    return;
                                }
                                if (EventItem.this.stars.size() > 0)
                                    EventItem.this.stars.remove(0);

                            }
                        });
            }
        }

        isStarred = starred;
    }

    /**
     * Adds a star to the item and ensures no duplicates
     * @param star
     */
    public void addStar(Star star) {
        for(Star existing : this.stars) {
            if (existing.getId() == star.getId())
                return;
        }

        this.stars.add(star);
    }

    public int getStarsCount(){
        return this.stars.size();
    }

    public void clearStars(){
        this.stars.clear();
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

    /**
     * Custom toString for debugging
     * @return
     */
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

    @Override
    public int compareTo(@NonNull EventItem eventItem) {
        return this.getStarsCount() - eventItem.getStarsCount();
    }
}
