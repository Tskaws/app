package tskaws.app;

/**
 * Created by jj_re on 5/31/2017.
 */

public class EventItem {

    // Class member variables
    private String title;
    private String date;
    private String description;
    private String category;
    private String link;

    /*
    private Object pictures1;
    private Object pictures2;
    */

    public EventItem(){
        this.title = "";
        this.date = "";
        this.description = "";
        this.category = "";
        this.link = "";
    }

    public EventItem(String title, String date, String description, String category, String link) {
        this.title = title;
        this.date = date;
        this.description = description;
        this.category = category;
        this.link = link;
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
}
