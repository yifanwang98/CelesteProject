package celeste.comic_community_4_1.repository;


import celeste.comic_community_4_1.model.Follow;
import celeste.comic_community_4_1.model.EmbeddedClasses.FollowIndentity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FollowRepository extends JpaRepository<Follow, FollowIndentity> {

}

