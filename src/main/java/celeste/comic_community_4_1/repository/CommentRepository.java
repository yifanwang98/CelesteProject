package celeste.comic_community_4_1.repository;


import celeste.comic_community_4_1.model.Comment;
import celeste.comic_community_4_1.model.EmbeddedClasses.PostIndentity;
import celeste.comic_community_4_1.model.Post;
import celeste.comic_community_4_1.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, PostIndentity> {
    List<Comment> findByPost(Post post);

    List<Comment> findCommentByUser(User user);

    List<Comment> findCommentByCreatedAtAfterAndPost(Date after, Post post);

    long countCommentByPost(Post post);

    Comment findCommentByCommentID(long commentID);

    void deleteCommentByUser(User user);
    void deleteCommentByPost(Post post);

}

