package tskaws.app;

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

    // Getter for title
    public String getTitle() {
        return title;
    }
    // Setter for title
    public void setTitle(String title) {
        this.title = title;
    }

    // Getter for description
    public String getDescription() {
        return description;
    }
    // Setter for description
    public void setDescription(String description) {
        this.description = description;
    }

    // Getter for Category
    public String getCategory() {
        return category;
    }
    // Setter for Category
    public void setCategory(String category) {
        this.category = category;
    }

    // Getter for link
    public String getLink() {
        return link;
    }
    // Setter for link
    public void setLink(String link) {
        this.link = link;
    }

    /*  I commented these out until we make the object for pictures

    public String getPictures1() {
        return pictures1;
    }

    public void setPictures1(String pictures1) {
        this.pictures1 = pictures1;
    }

    public String getPictures2() {
        return pictures2;
    }

    public void setPictures2(String pictures2) {
        this.pictures2 = pictures2;
    }
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
}
