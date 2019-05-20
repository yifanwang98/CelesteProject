package celeste.comic_community_4_1.repository;


import celeste.comic_community_4_1.model.EmbeddedClasses.SeriesContentIndentity;
import celeste.comic_community_4_1.model.Series;
import celeste.comic_community_4_1.model.SeriesContent;
import celeste.comic_community_4_1.model.Work;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeriesContentRepository extends JpaRepository<SeriesContent, SeriesContentIndentity> {
    List<SeriesContent> findSeriesContentBySeriesContentIndentitySeries(Series series);

    List<SeriesContent> findSeriesContentBySeriesContentIndentitySeriesOrderByCreatedAtAsc(Series series);

    List<SeriesContent> findSeriesContentBySeriesContentIndentityWork(Work work);

    void deleteSeriesContentBySeriesContentIndentitySeries(Series series);
}

