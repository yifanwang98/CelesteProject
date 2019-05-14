package celeste.comic_community_4_1.repository;

import celeste.comic_community_4_1.model.ForgetPassword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ForgetPasswordRepository extends JpaRepository<ForgetPassword, Long> {
    boolean existsForgetPasswordByEncrypted(String encrypted);

    ForgetPassword findForgetPasswordByEncrypted(String encrypted);
}
