package celeste.comic_community_4_1.miscellaneous;

import java.util.ArrayList;
import java.util.List;

public class UploadPostDraft {

    private String description = "";
    private List<String> imageString = new ArrayList<>(9);
    private List<String> thumbnails = new ArrayList<>(9);

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void addImage(String string) {
        if (imageString.size() < 9)
            imageString.add(string);
    }

    public void addThumbnail(String string) {
        if (thumbnails.size() < 9)
            thumbnails.add(string);
    }

    public List<String> getImageString() {
        return imageString;
    }

    public boolean isValid() {
        return imageString.size() > 0;
    }

    public void remove(int index) {
        if (imageString.size() <= index || index < 0)
            return;
        this.imageString.remove(index);
        this.thumbnails.remove(index);
    }

    public List<String> getThumbnails() {
        return thumbnails;
    }

}
