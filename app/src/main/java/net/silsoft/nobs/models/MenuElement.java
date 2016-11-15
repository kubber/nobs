package net.silsoft.nobs.models;

public class MenuElement {

    public final String TYPE_DIAGRAM="diagram";
    public final String TYPE_MENU="menu";
    public final String TYPE_ACTIVITY="activity";

    private String title;
    private String type;
    private String link;

    public MenuElement(String title, String type, String link) {
        this.title = title;
        this.type = type;
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
