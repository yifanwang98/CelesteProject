package celeste.comic_community_4_1.miscellaneous;

import java.util.List;

public class HashtagResultData {

    public final String hashtag;

    public final List<String> postImageList;
    public final List<Long> postIdList;

    public final List<String> seriesImageList;
    public final List<Long> seriesIdList;

    public HashtagResultData(String hashtag,
                             List<String> postImageList, List<Long> postIdList,
                             List<String> seriesImageList, List<Long> seriesIdList) {
        this.hashtag = hashtag;
        this.postImageList = postImageList;
        this.postIdList = postIdList;
        this.seriesImageList = seriesImageList;
        this.seriesIdList = seriesIdList;
    }
}
