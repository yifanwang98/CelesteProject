package celeste.comic_community_4_1.repository;


import celeste.comic_community_4_1.model.EmbeddedClasses.PostIndentity;
import celeste.comic_community_4_1.model.Like;
import celeste.comic_community_4_1.model.Post;
import celeste.comic_community_4_1.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface LikeRepository extends JpaRepository<Like, PostIndentity> {
    List<Like> findByPostIndentityPost(Post post);
    List<Like> findByPostIndentityUser(User user);

    List<Like> findLikeByCreatedAtAfterAndPostIndentityPost(Date after, Post post);

    boolean existsLikeByPostIndentityPostAndPostIndentityUser(Post post, User user);

    long countLikeByPostIndentityPost(Post post);

    void deleteLikeByPostIndentityUser(User user);

    void deleteLikeByPostIndentityPost(Post post);
}

