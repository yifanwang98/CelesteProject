package celeste.comic_community_4_1.miscellaneous;

public class HashTagData {

    public final String hashtag;

    public final long postCount;
    public final long seriesCount;

    public HashTagData(String hashtag, long postCount, long seriesCount) {
        this.hashtag = hashtag;
        this.postCount = postCount;
        this.seriesCount = seriesCount;
    }
}
