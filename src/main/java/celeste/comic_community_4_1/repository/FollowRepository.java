package celeste.comic_community_4_1.repository;


import celeste.comic_community_4_1.model.Follow;
import celeste.comic_community_4_1.model.EmbeddedClasses.FollowIndentity;
import celeste.comic_community_4_1.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FollowRepository extends JpaRepository<Follow, FollowIndentity> {
    List<Follow> findByFollowIndentityUserone(User user1);
    List<Follow> findByFollowIndentityUsertwo(User user2);
}
