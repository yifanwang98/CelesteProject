package celeste.comic_community_4_1.repository;


import celeste.comic_community_4_1.model.EmbeddedClasses.PostIndentity;
import celeste.comic_community_4_1.model.Post;
import celeste.comic_community_4_1.model.Star;
import celeste.comic_community_4_1.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StarRepository extends JpaRepository<Star, PostIndentity> {
    List<Star> findByPostIndentityPost(Post post);
    List<Star> findByPostIndentityUser(User user);

    boolean existsStarByPostIndentityPostAndPostIndentityUser(Post post, User user);

}

