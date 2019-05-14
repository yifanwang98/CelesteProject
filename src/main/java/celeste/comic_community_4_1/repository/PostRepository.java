package celeste.comic_community_4_1.repository;


import celeste.comic_community_4_1.model.Post;
import celeste.comic_community_4_1.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByUser(User user1);

    long countByoriginalPostIDAndIsRepost(long postid, Boolean b);

    List<Post> findByOriginalPostIDAndIsRepost(long postid, Boolean b);

    List<Post> findByCreatedAtAfterAndOriginalPostIDAndIsRepost(Date date, long postid, Boolean b);

    Post findPostByPostID(long postid);

    List<Post> findByPostID(long postId);

    List<Post> findByPrimaryGenre(String PrimaryGenre);

    List<Post> findBySecondaryGenre(String SecondaryGenre);

    long countPostByUser(User user);

    long countPostByCreatedAtAfterAndUserAndIsRepost(Date after, User user, boolean isRepost);
}

