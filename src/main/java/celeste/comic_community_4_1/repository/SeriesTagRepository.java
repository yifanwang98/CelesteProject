package celeste.comic_community_4_1.repository;

import celeste.comic_community_4_1.model.Series;
import celeste.comic_community_4_1.model.SeriesTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeriesTagRepository extends JpaRepository<SeriesTag, Long> {
    boolean existsSeriesTagBySeriesAndTag(Series series, String tag);

    int countSeriesTagBySeries(Series series);

    List<SeriesTag> findSeriesTagBySeries(Series series);

    long countSeriesTagByTag(String tag);

    List<SeriesTag> findSeriesTagByTag(String tag);

    void deleteSeriesTagBySeries(Series series);
}
