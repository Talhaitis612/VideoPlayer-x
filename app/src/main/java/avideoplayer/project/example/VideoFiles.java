package avideoplayer.project.example;

public class VideoFiles {
    String displayName;
    String duration;

    /* renamed from: id */
    String f119id;
    String path;
    String resolution;
    String size;
    String title;

    /* renamed from: wh */
    String f120wh;

    public VideoFiles(String id, String path2, String title2, String size2, String resolution2, String duration2, String displayName2, String wh) {
        this.f119id = id;
        this.path = path2;
        this.title = title2;
        this.size = size2;
        this.resolution = resolution2;
        this.duration = duration2;
        this.displayName = displayName2;
        this.f120wh = wh;
    }

    public String getId() {
        return this.f119id;
    }

    public void setId(String id) {
        this.f119id = id;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path2) {
        this.path = path2;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title2) {
        this.title = title2;
    }

    public String getSize() {
        return this.size;
    }

    public void setSize(String size2) {
        this.size = size2;
    }

    public String getResolution() {
        return this.resolution;
    }

    public void setResolution(String resolution2) {
        this.resolution = resolution2;
    }

    public String getDuration() {
        return this.duration;
    }

    public void setDuration(String duration2) {
        this.duration = duration2;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public void setDisplayName(String displayName2) {
        this.displayName = displayName2;
    }

    public String getWh() {
        return this.f120wh;
    }

    public void setWh(String wh) {
        this.f120wh = wh;
    }
}
