package celeste.comic_community_4_1.repository;


import celeste.comic_community_4_1.model.Comment;
import celeste.comic_community_4_1.model.EmbeddedClasses.PostIndentity;
import celeste.comic_community_4_1.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, PostIndentity> {
    List<Comment> findByPost(Post post);

    List<Comment> findCommentByCreatedAtAfterAndPost(Date after, Post post);

    long countCommentByPost(Post post);

}

