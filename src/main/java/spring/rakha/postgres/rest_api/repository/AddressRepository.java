package spring.rakha.postgres.rest_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spring.rakha.postgres.rest_api.entity.Address;
import spring.rakha.postgres.rest_api.entity.Contact;
import spring.rakha.postgres.rest_api.entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Integer> {

    Optional<Address> findFirstByContactAndId(Contact contact, Integer addressId);

    List<Address> findAllByContact(Contact contact);
}
