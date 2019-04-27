package celeste.comic_community_4_1.repository;

import celeste.comic_community_4_1.model.EmbeddedClasses.SeriesFollowIndentity;
import celeste.comic_community_4_1.model.Series;
import celeste.comic_community_4_1.model.SeriesFollow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeriesFollowRepository extends JpaRepository<SeriesFollow, SeriesFollowIndentity> {
    List<SeriesFollow> findBySeriesFollowIndentitySeries(Series series);
}

