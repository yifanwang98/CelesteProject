package celeste.comic_community_4_1.repository;


import celeste.comic_community_4_1.model.Star;
import celeste.comic_community_4_1.model.EmbeddedClasses.PostIndentity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StarRepository extends JpaRepository<Star, PostIndentity> {

}

