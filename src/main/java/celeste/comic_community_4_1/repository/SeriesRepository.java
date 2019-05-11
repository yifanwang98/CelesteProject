package celeste.comic_community_4_1.repository;

import celeste.comic_community_4_1.model.Series;
import celeste.comic_community_4_1.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeriesRepository extends JpaRepository<Series, Long> {
    List<Series> findByUser(User user);

    boolean existsSeriesBySeriesID(long id);

    Series findSeriesBySeriesID(long id);

    long countSeriesByUser(User user);

    List<Series> findSeriesBySeriesNameContains(String key);
}
