package celeste.comic_community_4_1.miscellaneous;

public class SearchResult implements Comparable<SearchResult> {

    public static final int USER_WEIGHT = 3;
    public static final int SERIES_WEIGHT = 2;
    public static final int POST_WEIGHT = 1;

    public String resultType;

    public PostData postData;
    public SeriesData seriesData;
    public UserData userData;
    public HashTagData hashTagData;

    public double relavence;

    @Override
    public int compareTo(SearchResult o) {
        if (this.relavence > o.relavence)
            return -1;
        if (this.relavence == o.relavence)
            return 0;
        return 1;
    }
}
