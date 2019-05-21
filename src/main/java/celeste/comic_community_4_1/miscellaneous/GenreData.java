package celeste.comic_community_4_1.miscellaneous;

public class GenreData implements Comparable<GenreData> {
    public final String genreName;
    public final long total;
    public final String thumbnail;

    public GenreData(String genreName, long total, String thumbnail) {
        this.genreName = genreName;
        this.total = total;
        this.thumbnail = thumbnail;
    }

    @Override
    public int compareTo(GenreData o) {
        if (this.total == o.total) {
            return this.genreName.compareTo(o.genreName);
        }
        if (this.total < o.total) {
            return 1;
        }
        return -1;
    }
}
