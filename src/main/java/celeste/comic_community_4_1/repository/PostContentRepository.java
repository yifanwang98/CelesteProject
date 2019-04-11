package celeste.comic_community_4_1.repository;


import celeste.comic_community_4_1.model.Post;
import celeste.comic_community_4_1.model.PostContent;
import celeste.comic_community_4_1.model.EmbeddedClasses.PostContentIndentity;
import celeste.comic_community_4_1.model.Work;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostContentRepository extends JpaRepository<PostContent, PostContentIndentity> {
    List<PostContent> findByPostIndentityPost(Post post);
    List<PostContent> findByPostIndentityWork(Work work);
}

