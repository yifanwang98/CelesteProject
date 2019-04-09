package celeste.comic_community_4_1.repository;


import celeste.comic_community_4_1.model.Like;
import celeste.comic_community_4_1.model.EmbeddedClasses.PostIndentity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeRepository extends JpaRepository<Like, PostIndentity> {

}
