package tskaws.app;

public class Star {
    private String id;
    private String rev;

    public Star(String id, String rev) {
        this.id = id;
        this.rev = rev;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRev() {
        return rev;
    }

    public void setRev(String rev) {
        this.rev = rev;
    }
}
