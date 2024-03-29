package celeste.comic_community_4_1.repository;


import celeste.comic_community_4_1.model.EmbeddedClasses.FollowIndentity;
import celeste.comic_community_4_1.model.Follow;
import celeste.comic_community_4_1.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface FollowRepository extends JpaRepository<Follow, FollowIndentity> {
    List<Follow> findByFollowIndentityUserone(User user1);
    List<Follow> findByFollowIndentityUsertwo(User user2);

    List<Follow> findFollowByFollowIndentityUseroneOrFollowIndentityUsertwo(User a, User b);

    long countFollowByFollowIndentityUserone(User user1);

    long countFollowByFollowIndentityUsertwo(User user2);

    boolean existsFollowByFollowIndentityUseroneAndFollowIndentityUsertwo(User user1, User user2);

    List<Follow> findByFollowIndentityUseroneUsernameAndFollowIndentityUsertwoUsername(String user1, String user2);

    Follow findFollowByFollowIndentityUseroneAndFollowIndentityUsertwo(User user1, User user2);

    long countFollowByCreatedAtAfterAndAndFollowIndentityUsertwo(Date after, User user);

    void deleteFollowByFollowIndentityUseroneOrFollowIndentityUsertwo(User user1, User user2);
}

