package celeste.comic_community_4_1.miscellaneous;

import celeste.comic_community_4_1.model.Post;

import java.util.List;

public class PostData {
    public final Post post;
    public final Post originalPost;
    public final List<String> images;

    public final long shareCount;
    public final long commentCount;
    public final long starCount;
    public final long likeCount;

    public final boolean starred;
    public final boolean liked;

    public PostData(Post post, Post originalPost, List<String> images,
                    long shareCount, long commentCount, long starCount, long likeCount,
                    boolean starred, boolean liked) {
        this.post = post;
        this.originalPost = originalPost;
        this.images = images;
        this.shareCount = shareCount;
        this.commentCount = commentCount;
        this.starCount = starCount;
        this.likeCount = likeCount;
        this.starred = starred;
        this.liked = liked;
    }
}
