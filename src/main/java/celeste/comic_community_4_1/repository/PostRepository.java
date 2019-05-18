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

    List<Post> findPostByUserAndCreatedAtBetween(User user, Date after, Date before);

    long countByoriginalPostIDAndIsRepost(long postid, Boolean b);

    List<Post> findByOriginalPostIDAndIsRepost(long postid, Boolean b);

    List<Post> findByCreatedAtAfterAndOriginalPostIDAndIsRepost(Date date, long postid, Boolean b);

    Post findPostByPostID(long postid);

    List<Post> findByPostID(long postId);

    List<Post> findByPrimaryGenre(String primaryGenre);

    List<Post> findBySecondaryGenre(String secondaryGenre);

    Post findFirstByPrimaryGenreOrSecondaryGenre(String genre, String genre2);

    long countPostByPrimaryGenreOrSecondaryGenre(String genre, String genre2);

    List<Post> findDistinctByPrimaryGenreOrSecondaryGenre(String genre, String genre2);

    long countPostByUser(User user);

    long countPostByCreatedAtAfterAndUserAndIsRepost(Date after, User user, boolean isRepost);

    List<Post> findPostsByCreatedAtAfterAndIsRepostAndUserIsNot(Date after, boolean isRepost, User excludedUser);
}

