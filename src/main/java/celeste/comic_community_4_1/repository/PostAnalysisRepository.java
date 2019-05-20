package celeste.comic_community_4_1.repository;

import celeste.comic_community_4_1.model.Post;
import celeste.comic_community_4_1.model.PostAnalysis;
import celeste.comic_community_4_1.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface PostAnalysisRepository extends JpaRepository<PostAnalysis, Long> {

    boolean existsPostAnalysisByPostAndUserAndViewedAt(Post post, User user, Date date);

    long countPostAnalysisByViewedAtAfterAndPost(Date date, Post post);

    void deletePostAnalysisByPost(Post post);

    List<PostAnalysis> findPostAnalysisByPost(Post post);
}
