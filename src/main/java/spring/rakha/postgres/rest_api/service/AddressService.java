package spring.rakha.postgres.rest_api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import spring.rakha.postgres.rest_api.entity.Address;
import spring.rakha.postgres.rest_api.entity.Contact;
import spring.rakha.postgres.rest_api.entity.User;
import spring.rakha.postgres.rest_api.model.AddressResponse;
import spring.rakha.postgres.rest_api.model.CreateAddressRequest;
import spring.rakha.postgres.rest_api.repository.AddressRepository;
import spring.rakha.postgres.rest_api.repository.ContactRepository;

import java.util.List;
import java.util.Objects;

@Service
public class AddressService {

    @Autowired
    private ValidationService validationService;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Transactional
    public AddressResponse create(User user, Integer contactId, CreateAddressRequest request) {
        validationService.validate(request);

        Contact contact = contactRepository.findFirstByUserAndId(user, contactId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact Not Found"));

        Address address = new Address();
        address.setCountry(request.getCountry());
        if (Objects.nonNull(request.getStreet())) {
            address.setStreet(request.getStreet());
        }
        if (Objects.nonNull(request.getCity())) {
            address.setCity(request.getCity());
        }
        if (Objects.nonNull(request.getProvince())) {
            address.setProvince(request.getProvince());
        }
        if (Objects.nonNull(request.getPostalCode())) {
            address.setPostalCode(request.getPostalCode());
        }

        address.setContact(contact);

        addressRepository.save(address);

        return toAddressResponse(address);
    }

    @Transactional(readOnly = true)
    public AddressResponse get(User user, Integer contactId, Integer addressId) {
        Contact contact = contactRepository.findFirstByUserAndId(user, contactId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact Not Found"));

        Address address = addressRepository.findFirstByContactAndId(contact, addressId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Address Not Found"));

        return toAddressResponse(address);
    }

    @Transactional
    public AddressResponse update(User user, Integer contactId, Integer addressId, CreateAddressRequest request) {
        validationService.validate(request);

        Contact contact = contactRepository.findFirstByUserAndId(user, contactId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact Not Found"));

        Address address = addressRepository.findFirstByContactAndId(contact, addressId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Address Not Found"));

        if (Objects.nonNull(request.getCountry())) {
            address.setCountry(request.getCountry());
        }
        if (Objects.nonNull(request.getStreet())) {
            address.setStreet(request.getStreet());
        }
        if (Objects.nonNull(request.getCity())) {
            address.setCity(request.getCity());
        }
        if (Objects.nonNull(request.getProvince())) {
            address.setProvince(request.getProvince());
        }
        if (Objects.nonNull(request.getPostalCode())) {
            address.setPostalCode(request.getPostalCode());
        }

        addressRepository.save(address);

        return toAddressResponse(address);
    }

    @Transactional
    public void delete(User user, Integer contactId, Integer addressId) {
        Contact contact = contactRepository.findFirstByUserAndId(user, contactId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact Not Found"));

        Address address = addressRepository.findFirstByContactAndId(contact, addressId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Address Not Found"));

        addressRepository.delete(address);
    }

    @Transactional(readOnly = true)
    public List<AddressResponse> list(User user, Integer contactId) {
        Contact contact = contactRepository.findFirstByUserAndId(user, contactId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact Not Found"));

        List<Address> addresses = addressRepository.findAllByContact(contact);

        return addresses.stream().map(this::toAddressResponse).toList();
    }

    private AddressResponse toAddressResponse(Address address) {
        return AddressResponse.builder()
                .street(address.getStreet())
                .city(address.getCity())
                .province(address.getProvince())
                .country(address.getCountry())
                .postalCode(address.getPostalCode())
                .build();
    }
}
