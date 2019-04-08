package celeste.comic_community_4_1.repository;

import celeste.comic_community_4_1.model.EmbeddedClasses.SeriesFollowIndentity;
import celeste.comic_community_4_1.model.SeriesFollow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeriesFollowRepository extends JpaRepository<SeriesFollow, SeriesFollowIndentity> {

}

