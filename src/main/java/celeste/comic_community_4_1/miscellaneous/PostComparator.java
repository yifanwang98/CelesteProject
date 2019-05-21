package celeste.comic_community_4_1.miscellaneous;

import celeste.comic_community_4_1.model.Post;

import java.util.Comparator;

public class PostComparator implements Comparator<Post> {

    @Override
    public int compare(Post o1, Post o2) {
        int i = o2.getCreatedAt().compareTo(o1.getCreatedAt());
        if (i == 0) {
            if (o1.getPostID() < o2.getPostID()) {
                return -1;
            } else if (o1.getPostID() == o2.getPostID()) {
                return 0;
            } else {
                return 1;
            }
        }
        return i;
    }

}
