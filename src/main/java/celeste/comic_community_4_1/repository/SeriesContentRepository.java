package celeste.comic_community_4_1.repository;


import celeste.comic_community_4_1.model.SeriesContent;
import celeste.comic_community_4_1.model.EmbeddedClasses.SeriesContentIndentity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeriesContentRepository extends JpaRepository<SeriesContent, SeriesContentIndentity> {

}

