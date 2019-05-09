package celeste.comic_community_4_1.miscellaneous;

import celeste.comic_community_4_1.model.Post;
import celeste.comic_community_4_1.model.Series;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

    public final Set<Series> fromSeries;

    public final List<String> postTags;

    public PostData(Post post, Post originalPost, List<String> images,
                    long shareCount, long commentCount, long starCount, long likeCount,
                    boolean starred, boolean liked, Set<Series> fromSeries, List<String> postTags) {
        this.post = post;
        this.originalPost = originalPost;
        this.images = images;
        this.shareCount = shareCount;
        this.commentCount = commentCount;
        this.starCount = starCount;
        this.likeCount = likeCount;
        this.starred = starred;
        this.liked = liked;
        this.fromSeries = fromSeries;
        this.postTags = postTags;
    }

    public PostData(Post post, Post originalPost, List<String> images, Set<Series> fromSeries) {
        this.post = post;
        this.originalPost = originalPost;
        this.images = images;
        this.shareCount = -1;
        this.commentCount = -1;
        this.starCount = -1;
        this.likeCount = -1;
        this.starred = false;
        this.liked = false;
        this.fromSeries = fromSeries;
        this.postTags = new ArrayList<>();
    }

    public PostData(Post post, Post originalPost, List<String> images,
                    boolean starred, boolean liked, Set<Series> fromSeries, List<String> postTags) {
        this.post = post;
        this.originalPost = originalPost;
        this.images = images;
        this.shareCount = -1;
        this.commentCount = -1;
        this.starCount = -1;
        this.likeCount = -1;
        this.starred = starred;
        this.liked = liked;
        this.fromSeries = fromSeries;
        this.postTags = postTags;
    }

    public String postCreatedAt() {
        return Notification.getDateString(post.getCreatedAt());
    }

    public String originalPostCreatedAt() {
        return Notification.getDateString(originalPost.getCreatedAt());
    }
}
