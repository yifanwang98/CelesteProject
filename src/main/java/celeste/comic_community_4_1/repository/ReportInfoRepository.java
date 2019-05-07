package celeste.comic_community_4_1.repository;

import celeste.comic_community_4_1.model.ReportInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ReportInfoRepository extends JpaRepository<ReportInfo, Long> {

}
