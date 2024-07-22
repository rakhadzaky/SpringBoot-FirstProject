package spring.rakha.postgres.rest_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spring.rakha.postgres.rest_api.entity.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findOneByUsername(String username);

    Optional<User> findFirstByToken(String token);
}
