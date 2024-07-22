package spring.rakha.postgres.rest_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import spring.rakha.postgres.rest_api.entity.Contact;
import spring.rakha.postgres.rest_api.entity.User;

import java.util.Optional;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Integer> , JpaSpecificationExecutor<Contact> {

    Optional<Contact> findFirstByUserAndId(User user, Integer id);
}
