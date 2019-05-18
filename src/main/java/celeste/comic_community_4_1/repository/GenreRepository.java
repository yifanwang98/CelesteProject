package celeste.comic_community_4_1.repository;

import celeste.comic_community_4_1.model.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GenreRepository extends JpaRepository<Genre, String> {
    boolean existsGenreByGenre(String genre);

    Genre findGenreByGenre(String genre);
}
