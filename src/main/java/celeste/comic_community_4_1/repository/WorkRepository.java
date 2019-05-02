package celeste.comic_community_4_1.repository;

import celeste.comic_community_4_1.model.Work;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkRepository extends JpaRepository<Work, Long> {
    List<Work> findByUserUsername(String username);
    List<Work> findByGenre(String genre);

    Work findWorkByWorkID(long id);
}
