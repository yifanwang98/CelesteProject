package celeste.comic_community_4_1.repository;

import celeste.comic_community_4_1.model.Post;
import celeste.comic_community_4_1.model.ReportInfo;
import celeste.comic_community_4_1.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;


@Repository
public interface ReportInfoRepository extends JpaRepository<ReportInfo, Long> {

    int countReportInfoByReporteeAndCreatedAtAfter(User user, Date after);

    boolean existsReportInfoByCreatedAtAfterAndReporteeAndReporter(Date after, User reportee, User reporter);

    List<ReportInfo> findReportInfoByReportee(User reportee);

    List<ReportInfo> findReportInfoByPost(Post post);

    List<ReportInfo> findReportInfoByReporteeOrReporter(User a, User b);

    void deleteReportInfoByReporterOrReportee(User reporter, User reportee);

    void deleteReportInfoByPost(Post post);
}
