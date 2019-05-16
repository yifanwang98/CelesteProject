package celeste.comic_community_4_1.repository;

import celeste.comic_community_4_1.model.SearchWords;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SearchWordsRepository extends JpaRepository<SearchWords, String> {
    List<SearchWords> findTop5ByOrderByHeatDesc();

    SearchWords findSearchWordsByWord(String word);

    List<SearchWords> findTop10ByOrderByHeatDesc();
}
