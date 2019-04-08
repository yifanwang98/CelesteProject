package celeste.comic_community_4_1.repository;


import celeste.comic_community_4_1.model.PostContent;
import celeste.comic_community_4_1.model.EmbeddedClasses.PostContentIndentity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostContentRepository extends JpaRepository<PostContent, PostContentIndentity> {

}

