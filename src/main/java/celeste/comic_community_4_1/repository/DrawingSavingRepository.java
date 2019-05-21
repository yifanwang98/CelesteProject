package celeste.comic_community_4_1.repository;

import celeste.comic_community_4_1.model.DrawingSaving;
import celeste.comic_community_4_1.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DrawingSavingRepository extends JpaRepository<DrawingSaving, Long> {
    List<DrawingSaving> findByUserone(User user1);

    DrawingSaving findDrawingSavingByUserone(User user);

    void deleteDrawingSavingByUserone(User user);
}
