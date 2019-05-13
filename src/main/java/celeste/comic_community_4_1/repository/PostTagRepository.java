package celeste.comic_community_4_1.repository;

import celeste.comic_community_4_1.model.Post;
import celeste.comic_community_4_1.model.PostTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostTagRepository extends JpaRepository<PostTag, Long> {

    boolean deletePostTagByPostAndTag(Post post, String tag);

    List<PostTag> findPostTagByPost(Post post);

    long countPostTagByTag(String tag);

    List<PostTag> findPostTagByTag(String tag);
}
