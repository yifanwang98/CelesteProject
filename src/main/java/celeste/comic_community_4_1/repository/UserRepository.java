package celeste.comic_community_4_1.repository;

import celeste.comic_community_4_1.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    List<User> findUserByUsernameContains(String substring);
}
