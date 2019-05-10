package celeste.comic_community_4_1.miscellaneous;

import java.util.ArrayList;
import java.util.List;

public class UploadPostDraft {

    public boolean isWiki = false;
    public long wikiSeriesID = -1;

    private List<String> imageString = new ArrayList<>(9);
    private List<String> thumbnails = new ArrayList<>(9);


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

    public boolean valid() {
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
