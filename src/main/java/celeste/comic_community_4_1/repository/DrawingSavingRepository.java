package celeste.comic_community_4_1.repository;

import celeste.comic_community_4_1.model.DrawingSaving;
import celeste.comic_community_4_1.model.EmbeddedClasses.DrawingSavingIndentity;
import celeste.comic_community_4_1.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DrawingSavingRepository extends JpaRepository<DrawingSaving, DrawingSavingIndentity> {
    Optional<DrawingSaving> findByDrawingSavingIndentityUserone(User user);
}
